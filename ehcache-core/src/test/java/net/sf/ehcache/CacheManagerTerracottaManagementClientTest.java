package net.sf.ehcache;

import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.MockCacheCluster;
import net.sf.ehcache.terracotta.TerracottaClient;
import net.sf.ehcache.terracotta.TerracottaUnitTesting;
import org.junit.*;

import java.io.File;
import java.lang.reflect.Field;

import static net.sf.ehcache.AbstractCacheTest.TEST_CONFIG_DIR;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Saurabh Agarwal on 16/11/16.
 */
public class CacheManagerTerracottaManagementClientTest {

  @Before
  public void setUp() throws Exception {
    ClusteredInstanceFactory mockFactory = mock(ClusteredInstanceFactory.class);
    TerracottaUnitTesting.setupTerracottaTesting(mockFactory);

    CacheCluster mockCacheCluster = new MockCacheCluster();
    when(mockFactory.getTopology()).thenReturn(mockCacheCluster);
  }

  /*
   * Category 1: CacheManager start tests
   */

  @Test
  // mgmtTerracottaClient should be initialized to the first clustered CacheManager
  public void mgmtTerracottaClientInitializerCheckForClusteredCM() throws NoSuchFieldException, IllegalAccessException {
    // mgmtTerracottaClient should be null before the creation of any clustered caches
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));

    // Using Reflection to access the private member terracottaClient of CacheManager class
    Field privateField = CacheManager.class.getDeclaredField("terracottaClient");
    privateField.setAccessible(true);

    TerracottaClient terracottaClient = (TerracottaClient) privateField.get(cacheManager);

    // mgmtTerracottaClient should now be initialized to the new created clustered CacheManager
    assertSame(terracottaClient, CacheManager.mgmtTerracottaClient);

    assertEquals(2, CacheManager.ALL_CACHE_MANAGERS.size());

    cacheManager.shutdown();

    assertEquals(0, CacheManager.ALL_CACHE_MANAGERS.size());
    assertNull(CacheManager.mgmtTerracottaClient);
  }

  @Test
  // mgmtTerracottaClient should not be initialized to any standalone CacheManagers
  public void mgmtTerracottaClientInitializerCheckForStandaloneCM() {
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager1 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-standaloneCM-simple1.xml")));

    // mgmtTerracottaClient should be null
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager2 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-standaloneCM-simple2.xml")));
    assertEquals(2, CacheManager.ALL_CACHE_MANAGERS.size());

    // mgmtTerracottaClient should still be null
    assertNull(CacheManager.mgmtTerracottaClient);
    cacheManager1.shutdown();
    cacheManager2.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);
    assertEquals(0, CacheManager.ALL_CACHE_MANAGERS.size());
  }

  /*
   * Category 2: CacheManager shutdown tests
   */

  @Test
  // Create one clustered CacheManager and shut it down
  public void createAndShutdownClusteredCMTest() {
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));
    assertNotNull(CacheManager.mgmtTerracottaClient);

    cacheManager.shutdown();

    assertNull(CacheManager.mgmtTerracottaClient);
  }

  @Test
  // Create one standalone CacheManager and shut it down
  public void createAndShutdownStandaloneCMTest() {
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-standaloneCM-simple1.xml")));

    // mgmtTerracottaClient isn't created for standalone CacheManagers,and should be null
    assertNull(CacheManager.mgmtTerracottaClient);
    assertEquals(1, CacheManager.ALL_CACHE_MANAGERS.size());

    cacheManager.shutdown();

    // mgmtTerracottaClient should still be null
    assertNull(CacheManager.mgmtTerracottaClient);
    assertEquals(0, CacheManager.ALL_CACHE_MANAGERS.size());
  }

  @Test
  // Create two clustered CacheManagers and test for mgmtTerracottaClient correctness
  public void createTwoClusteredCMAndShutdownFirstTest() {
    assertNull(CacheManager.mgmtTerracottaClient);

    CacheManager cacheManager1 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));
    TerracottaClient terracottaClient = CacheManager.mgmtTerracottaClient;

    assertNotNull(terracottaClient);

    CacheManager cacheManager2 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple2.xml")));
    assertSame(terracottaClient, CacheManager.mgmtTerracottaClient);

    cacheManager1.shutdown();
    assertSame(terracottaClient, CacheManager.mgmtTerracottaClient);

    cacheManager2.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);
  }

  @Test
  /* Create two clustered CacheManagers, shut down the second. Should still be able to see the first CacheManager and
   * mgmtTerracottaClient should point to the same.
   */
  public void createTwoClusteredCMAndShutdownSecondTest() {
    CacheManager cacheManager1 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));
    TerracottaClient terracottaClient = CacheManager.mgmtTerracottaClient;

    CacheManager cacheManager2 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple2.xml")));

    cacheManager2.shutdown();
    assertSame(terracottaClient, CacheManager.mgmtTerracottaClient);

    cacheManager1.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);
  }

  @Test
  /* Create one standalone CM and one clustered CM (in that order) and check for mgmtTerracottaClient
   */
  public void createOneStandaloneAndOneClusteredTest() {
    assertNull(CacheManager.mgmtTerracottaClient);
    CacheManager cacheManager1 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-standaloneCM-simple1.xml")));

    assertNull(CacheManager.mgmtTerracottaClient);
    CacheManager cacheManager2 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));

    assertNotNull(CacheManager.mgmtTerracottaClient);
    assertEquals(3, CacheManager.ALL_CACHE_MANAGERS.size());

    cacheManager2.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);

    cacheManager1.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);
    assertEquals(0, CacheManager.ALL_CACHE_MANAGERS.size());
  }

  @Test
  /* Create one clustered CM and one standalone CM (in that order) and check for mgmtTerracottaClient
   */
  public void createOneClusteredAndOneStandaloneTest() {
    assertNull(CacheManager.mgmtTerracottaClient);
    CacheManager cacheManager1 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-clusteredCM-simple1.xml")));

    // mgmtTerracottaClient should be initialized now
    assertNotNull(CacheManager.mgmtTerracottaClient);
    CacheManager cacheManager2 = new CacheManager(ConfigurationFactory.parseConfiguration(new File(TEST_CONFIG_DIR + "ehcache-standaloneCM-simple1.xml")));

    assertEquals(3, CacheManager.ALL_CACHE_MANAGERS.size());
    cacheManager1.shutdown();
    assertNull(CacheManager.mgmtTerracottaClient);

    cacheManager2.shutdown();

    // mgmtTerracottaClient should be null now
    assertNull(CacheManager.mgmtTerracottaClient);
    assertEquals(0, CacheManager.ALL_CACHE_MANAGERS.size());
  }
}
