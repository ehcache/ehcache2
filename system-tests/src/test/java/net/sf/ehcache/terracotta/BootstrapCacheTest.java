/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is
 *      Terracotta, Inc., a Software AG company
 */
package net.sf.ehcache.terracotta;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.CacheStoreHelper;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.test.util.WaitUtil;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import junit.framework.Assert;

/**
 * @author Alex Snaps
 */
public class BootstrapCacheTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT        = 3;

  private static final int ELEMENTS_PER_NODE = 10;

  public BootstrapCacheTest(TestConfig testConfig) {
    super(testConfig, App.class, App.class, App.class);
  }

  public static class App extends ClientBase {

    public App(String[] args) {
      super(args);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    public void doTest() throws Exception {
      final int index = getBarrierForAllClients().await();

      Assert.assertEquals(0, KeySnapshotter.getKnownCacheManagers().size());
      cacheManager = createCacheManager(index);
      getBarrierForAllClients().await();
      Cache cache = cacheManager.getCache("test");
      final KeySnapshotter snapshotter = ((TerracottaBootstrapCacheLoader) cache.getBootstrapCacheLoader())
          .getKeySnapshotter();
      final CyclicBarrier waitSnapshot = new CyclicBarrier(2);
      snapshotter.setOnSnapshot(new Runnable() {
        @Override
        public void run() {
          try {
            waitSnapshot.await();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      });

      getBarrierForAllClients().await();
      waitSnapshot.await(); // WRITING NOTHING TO DISK
      for (int i = 0; i < ELEMENTS_PER_NODE; i++) {
        cache.put(new Element("key" + i + "_node" + index, "value for key"));
      }
      System.out.println(new Date() + " ==> node" + index + " put 10 elements in the cache");
      getBarrierForAllClients().await();

      if (index == 0) {
        Assert.assertEquals(ELEMENTS_PER_NODE * NODE_COUNT, cache.getSize());
      }
      Assert.assertEquals(1, KeySnapshotter.getKnownCacheManagers().size());
      Assert.assertEquals(true, KeySnapshotter.getKnownCacheManagers().contains(cacheManager));
      waitSnapshot.await(); // WRITING THE LOCAL KEY SET TO DISK
      System.out.println(new Date() + " ==> node" + index + " CacheManager shutdown1...");
      cacheManager.shutdown(); // IMMEDIATE SHUTDOWN
      getBarrierForAllClients().await();
      cacheManager = createCacheManager(index);
      cache = cacheManager.getCache("test");

      final KeySnapshotter keySnapshotter = ((TerracottaBootstrapCacheLoader) cache.getBootstrapCacheLoader())
          .getKeySnapshotter();
      keySnapshotter.doSnapshot();

      final Store store = new CacheStoreHelper(cache).getStore();
      final Set localKeys = ((TerracottaStore) store).getLocalKeys();
      for (Object localKey : localKeys) {
        final String key = (String) localKey;
        Assert.assertEquals("_node" + index, key.substring(key.indexOf("_"), key.length()));
      }
      Assert.assertEquals("For node" + index, ELEMENTS_PER_NODE, localKeys.size());
      getBarrierForAllClients().await();
      gc();
      getBarrierForAllClients().await();

      WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          gc();
          Collection<CacheManager> knownCacheManagers = KeySnapshotter.getKnownCacheManagers();
          System.out.println("Known cacheMnaagers: " + knownCacheManagers.size());
          for (CacheManager cm : knownCacheManagers) {
            System.out.println("   name: " + cm.getName());
          }
          return knownCacheManagers.size() == 1;
        }
      });
      Assert.assertEquals("For node " + index, true, KeySnapshotter.getKnownCacheManagers().contains(cacheManager));
      getBarrierForAllClients().await();
      cacheManager.shutdown();
      System.out.println(new Date() + " ==> node" + index + " CacheManager shutdown2...");
      pass();
    }

    private void gc() throws InterruptedException {
      for (int i = 0; i < 20; i++) {
        System.gc();
      }
      Thread.sleep(2000);
      for (int i = 0; i < 20; i++) {
        System.gc();
      }
    }

    @Override
    protected CacheManager getCacheManager() {

      return null;
    }

    protected CacheManager createCacheManager(final int index) {

      final Configuration configuration = new Configuration().name("bootstrap-cache-test");
      TerracottaClientConfiguration tcConfiguration = new TerracottaClientConfiguration();
      tcConfiguration.setUrl(getTerracottaUrl());
      tcConfiguration.setRejoin(true);
      configuration.addTerracottaConfig(tcConfiguration);

      configuration
          .addCache(new CacheConfiguration("test", 20)
              .eternal(true)
              .terracotta(new TerracottaConfiguration().clustered(true)
                              .consistency(TerracottaConfiguration.Consistency.STRONG))
              .bootstrapCacheLoaderFactory(new CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration()
                                               .className(net.sf.ehcache.terracotta.TerracottaBootstrapCacheLoaderFactory.class
                                                              .getName())
                                               .properties("bootstrapAsynchronously=false" + ",directory=dumps_node"
                                                               + index + ",interval=15" + ",immediateShutdown=true"
                                                               + ",doKeySnapshot=true"
                                                               + ",doKeySnapshotOnDedicatedThread=false")
                                               .propertySeparator(",")));
      return new CacheManager(configuration);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) {
      // Added to fix compilation error. This will never get called for this test as it overrides the run method of
      // ClientBase.
    }

  }

}
