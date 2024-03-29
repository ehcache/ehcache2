/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package net.sf.ehcache.management.service.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ClusteredInstanceFactoryAccessor;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.TerracottaClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

/**
 * @author Ludovic Orban
 */
public class DfltSamplerRepositoryServiceV2Test {

  private DfltSamplerRepositoryServiceV2 repositoryService;
  private ClusteredInstanceFactory clusteredInstanceFactory;
  private CacheManager cacheManager;

  @Before
  public void setUp() throws Exception {
    ManagementRESTServiceConfiguration managementRESTServiceConfiguration = new ManagementRESTServiceConfiguration();
    managementRESTServiceConfiguration.setEnabled(true);
    RemoteAgentEndpointImpl remoteAgentEndpoint = mock(RemoteAgentEndpointImpl.class);
    repositoryService = new DfltSamplerRepositoryServiceV2(managementRESTServiceConfiguration, remoteAgentEndpoint);

    Configuration configuration = new Configuration();
    configuration.setName("testCacheManager");
    CacheConfiguration cacheConfiguration = new CacheConfiguration("testCache1", 12);
    configuration.addCache(cacheConfiguration);
    cacheManager = new CacheManager(configuration);
    // Cache ehcache = new Cache(cacheConfiguration);
    TerracottaClient terracottaClient = mock(TerracottaClient.class);
    clusteredInstanceFactory = mock(ClusteredInstanceFactory.class);

    ClusteredInstanceFactoryAccessor.setTerracottaClient(cacheManager, terracottaClient);
    when(terracottaClient.getClusteredInstanceFactory()).thenReturn(clusteredInstanceFactory);

    repositoryService.register(cacheManager);
  }

  @Test
  public void testCreateCacheEntitiesDisablesNonStop() throws Exception {
    repositoryService.createCacheEntities(Collections.singleton("testCacheManager"),
        Collections.singleton("testCache1"),
        Collections.singleton("Size"));

    verify(clusteredInstanceFactory, times(4)).enableNonStopForCurrentThread(anyBoolean());
  }

  @Test
  public void testCreateCacheStatisticSampleEntityDisablesNonStop() throws Exception {
    repositoryService.createCacheStatisticSampleEntity(Collections.singleton("testCacheManager"),
        Collections.singleton("testCache1"),
        Collections.singleton("Size"));

    verify(clusteredInstanceFactory, times(4)).enableNonStopForCurrentThread(anyBoolean());
  }

  @Test
  public void testClearCacheDisablesNonStop() throws Exception {
    repositoryService.clearCache("testCacheManager", "testCache1");

    verify(clusteredInstanceFactory, times(4)).enableNonStopForCurrentThread(anyBoolean());
  }

  @Test
  public void testCanAddDecoratedCache() {
    Cache underlyingCache = new Cache(new CacheConfiguration("decoratedTestCache", 10));
    cacheManager.addCache(new BlockingCache(underlyingCache));
    // not using cacheEventListeners for management anymore : TAB-5077
    assertThat(underlyingCache.getCacheEventNotificationService().hasCacheEventListeners(), is(false));
  }

  @Test
  public void testPropertyListenerRemoval() throws Exception {
    CacheManager cacheManagerSpy = spy(cacheManager);
    repositoryService.unregister(cacheManagerSpy);
    verify(cacheManagerSpy).getConfiguration();
    repositoryService.register(cacheManager);
  }

  @After
  public void tearDown() {
    CacheManager.getCacheManager("testCacheManager").shutdown();
  }

}
