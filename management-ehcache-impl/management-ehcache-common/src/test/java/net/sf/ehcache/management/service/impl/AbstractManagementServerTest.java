package net.sf.ehcache.management.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.management.AbstractManagementServer;
import net.sf.ehcache.management.service.ManagementServerLifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.embedded.StandaloneServer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 
 * THis test verifies basic interaction between AbstractManagementServer and its dependencies
 * PowerMock is used to mock StandaloneServer, which is a final class.
 * 
 * @author Anthony Dahanne
 *
 */
public class AbstractManagementServerTest {

  private CacheManager cacheManager;

  @Before
  public void setUp() throws Exception {
    ServiceLocator.unload();
    cacheManager = mock(CacheManager.class);
  }

  @After
  public void tearDown() {
    cacheManager.shutdown();
  }

  @Test
  /**
   * Verifies that managementServer.start() calls server.start()
   * @throws Exception
   */
  public void startTest() throws Exception {
    StandaloneServer serverMock = mock(StandaloneServer.class);
    ManagementServer managementServer = new ManagementServer(serverMock, null);
    managementServer.start();
    verify(serverMock).start();
  }

  @Test(expected = CacheException.class)
  /**
   * Verifies that managementServer.start() calls server.start() and rethrows the exception
   * @throws Exception
   */
  public void startTestException() throws Exception {
    StandaloneServer serverMock = mock(StandaloneServer.class);
    ManagementServerLifecycle sampleRepositoryServiceMock = mock(ManagementServerLifecycle.class);
    ManagementServer managementServer = new ManagementServer(serverMock, sampleRepositoryServiceMock);
    doThrow(new CacheException()).when(serverMock).start();
    managementServer.start();
  }

  @Test
  public void stopTest() throws Exception {
    StandaloneServer serverMock = mock(StandaloneServer.class);
    ManagementServerLifecycle sampleRepositoryServiceMock = mock(ManagementServerLifecycle.class);
    ManagementServer managementServer = new ManagementServer(serverMock, sampleRepositoryServiceMock);

    // stop is also calling ServiceLocator.unload(); to make sure this is called, let's load it first
    // and assert it is unloaded after stop()
    managementServer.loadEmbeddedAgentServiceLocatorWithStringClass();
    assertNotNull(ServiceLocator.locate(String.class));

    managementServer.stop();

    IllegalStateException exceptionThrown = null;
    try {
      // since the servicelocator is unloaded, it should throw an exception
      ServiceLocator.locate(String.class);
    } catch (IllegalStateException e) {
      exceptionThrown = e;
    }
    assertNotNull(exceptionThrown);
  }

  @Test
  public void registerTest() {
    ManagementServerLifecycle sampleRepositoryServiceMock = mock(ManagementServerLifecycle.class);
    ManagementServer managementServer = new ManagementServer(null, sampleRepositoryServiceMock);
    managementServer.register(cacheManager);
    verify(sampleRepositoryServiceMock).register(cacheManager);
  }

  @Test
  public void unregisterTest() {
    ManagementServerLifecycle sampleRepositoryServiceMock = mock(ManagementServerLifecycle.class);
    ManagementServer managementServer = new ManagementServer(null, sampleRepositoryServiceMock);
    managementServer.unregister(cacheManager);
    verify(sampleRepositoryServiceMock).unregister(cacheManager);
  }

  @Test
  public void hasRegisteredTest() {
    ManagementServerLifecycle sampleRepositoryServiceMock = mock(ManagementServerLifecycle.class);
    ManagementServer managementServer = new ManagementServer(null, sampleRepositoryServiceMock);
    when(sampleRepositoryServiceMock.hasRegistered()).thenReturn(Boolean.TRUE);
    managementServer.register(cacheManager);
    assertTrue(managementServer.hasRegistered());
  }

  class ManagementServer extends AbstractManagementServer {

    ManagementServer(StandaloneServer server, ManagementServerLifecycle repositoryService) {
      this.standaloneServer = server;
      this.managementServerLifecycles.add(repositoryService);
    }

    protected void loadEmbeddedAgentServiceLocatorWithStringClass() {
      ServiceLocator locator = new ServiceLocator();
      locator.loadService(String.class, new String());
      // service locator is initialized with 1 service : String
      ServiceLocator.load(locator);
    }

    @Override
    public void initialize(ManagementRESTServiceConfiguration configuration) {
    }

    @Override
    public void registerClusterRemoteEndpoint(String clientUUID) {
    }

    @Override
    public void unregisterClusterRemoteEndpoint(String clientUUID) {
    }
  }


}
