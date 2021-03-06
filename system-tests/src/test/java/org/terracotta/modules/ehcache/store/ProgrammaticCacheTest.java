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
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

/**
 * Basic cache test for creating clustered caches programmatically
 */
public class ProgrammaticCacheTest extends AbstractCacheTestBase {
  private static final int NODE_COUNT = 3;

  public ProgrammaticCacheTest(TestConfig testConfig) {
    super(testConfig, App.class, App.class, App.class);
  }

  public static class App extends ClientBase {
    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super(args);
      this.barrier = getClusteringToolkit().getBarrier("test-barrier", NODE_COUNT);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache testcache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      String cacheName = "regionName";
      Cache cache = new Cache(new CacheConfiguration(cacheName, 1000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
          .timeToLiveSeconds(300)
          .timeToIdleSeconds(300)
          .clearOnFlush(true)
          .terracotta(new TerracottaConfiguration().clustered(true)
                          .coherentReads(true).orphanEviction(true).orphanEvictionPeriod(4).localKeyCache(false)
                          .localKeyCacheSize(0).copyOnRead(false)));

      cacheManager.addCache(cache);
      barrier.await();
      cache = cacheManager.getCache(cacheName);

      Assert.assertEquals(0, cache.getSize());

      barrier.await();

      if (index == 0) {
        cache.put(new Element("key", "value"));
      }

      barrier.await();

      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", cache.get("key").getObjectValue());

      barrier.await();

      if (index == 0) {
        boolean removed = cache.remove("key");
        Assert.assertTrue(removed);
      }

      barrier.await();

      Assert.assertEquals(0, cache.getSize());
    }

  }

}
