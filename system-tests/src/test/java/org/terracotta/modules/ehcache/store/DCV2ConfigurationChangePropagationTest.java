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
import net.sf.ehcache.CacheManager;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import com.tc.test.config.model.TestConfig;
import com.tc.util.concurrent.ThreadUtil;

import junit.framework.Assert;

/**
 * @author cdennis
 */
public class DCV2ConfigurationChangePropagationTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 3;

  public DCV2ConfigurationChangePropagationTest(TestConfig testConfig) {
    super("basic-dcv2-cache-test.xml", testConfig, App.class, App.class, App.class);
  }

  public static class App extends ClientBase {

    private final ToolkitBarrier barrier1;
    private final ToolkitBarrier barrier2;
    private int                  clientId;

    public App(String[] args) {
      super(args);
      this.barrier1 = getClusteringToolkit().getBarrier("barrier1", NODE_COUNT);
      this.barrier2 = getClusteringToolkit().getBarrier("barrier2", NODE_COUNT - 1);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      clientId = barrier1.await();
      cacheManager = getCacheManager();
      if (clientId != 2) {
        testTTIChange();
        testTTLChange();
        testDiskCapacityChange();
        testMemoryCapacityChange();
      }
      barrier1.await();
      if (clientId == 2) {
        verifyNewNode(cacheManager.getCache("dcv2Cache"));
      }
      barrier1.await();
    }

    private void verifyNewNode(final Cache cache) {
      Assert.assertEquals(99, cache.getCacheConfiguration().getTimeToIdleSeconds());
      Assert.assertEquals(99, cache.getCacheConfiguration().getTimeToLiveSeconds());
      Assert.assertEquals(99, cache.getCacheConfiguration().getMaxEntriesInCache());
      Assert.assertEquals(10000, cache.getCacheConfiguration().getMaxEntriesLocalHeap());
    }

    private CacheManager testTTIChange() throws Throwable {
      barrier2.await();

      cacheManager = getCacheManager();
      final Cache cache = cacheManager.getCache("dcv2Cache");
      cache.getCacheConfiguration().setEternal(false);

      int index = barrier2.await();

      if (index == 0) {
        System.err.println("Changing TTI on Client " + clientId);
        cache.getCacheConfiguration().setTimeToIdleSeconds(99);
      }

      barrier2.await();

      for (int i = 0; i < 60; i++) {
        Thread.sleep(1000);
        if (99 == cache.getCacheConfiguration().getTimeToIdleSeconds()) {
          System.err.println("Change to TTI took " + (i + 1) + " seconds to propagate to Client " + clientId);
          return cacheManager;
        }
      }

      Assert.fail("Change to TTI failed to propagate inside 1 minute");
      return cacheManager;
    }

    private CacheManager testTTLChange() throws Throwable {
      barrier2.await();

      cacheManager = getCacheManager();
      final Cache cache = cacheManager.getCache("dcv2Cache");
      cache.getCacheConfiguration().setEternal(false);

      int index = barrier2.await();

      if (index == 0) {
        System.err.println("Changing TTL on Client " + clientId);
        cache.getCacheConfiguration().setTimeToLiveSeconds(99);
      }

      barrier2.await();

      for (int i = 0; i < 60; i++) {
        Thread.sleep(1000);
        if (99 == cache.getCacheConfiguration().getTimeToLiveSeconds()) {
          System.err.println("Change to TTL took " + (i + 1) + " seconds to propagate to Client " + clientId);
          return cacheManager;
        }
      }

      Assert.fail("Change to TTL failed to propagate inside 1 minute");
      return cacheManager;
    }

    private CacheManager testDiskCapacityChange() throws Throwable {
      barrier2.await();

      cacheManager = getCacheManager();
      final Cache cache = cacheManager.getCache("dcv2Cache");

      int index = barrier2.await();

      if (index == 0) {
        System.err.println("Changing Disk Capacity on Client " + clientId);
        cache.getCacheConfiguration().setMaxEntriesInCache(99);
      }

      barrier2.await();

      for (int i = 0; i < 60; i++) {
        Thread.sleep(1000);
        if (99 == cache.getCacheConfiguration().getMaxEntriesInCache()) {
          System.err.println("Change to Disk Capacity took " + (i + 1) + " seconds to propagate to Client " + clientId);
          return cacheManager;
        }
      }

      Assert.fail("Change to Disk Capacity failed to propagate inside 1 minute");
      return cacheManager;
    }

    private CacheManager testMemoryCapacityChange() throws Throwable {
      barrier2.await();

      cacheManager = getCacheManager();
      final Cache cache = cacheManager.getCache("dcv2Cache");

      int index = barrier2.await();

      if (index == 0) {
        System.err.println("Changing Memory Capacity on Client " + clientId);
        cache.getCacheConfiguration().setMaxEntriesLocalHeap(99);
      }

      barrier2.await();

      if (index == 0) {
        Assert.assertEquals("Failed to change max entries local heap.", 99, cache.getCacheConfiguration()
            .getMaxEntriesLocalHeap());
      } else {
        System.out.println("client " + clientId + " is gonna wait for 60 secs...");
        ThreadUtil.reallySleep(60 * 1000);
        Assert.assertEquals("Max entries local heap change propagated to the other client.", 10000, cache
            .getCacheConfiguration().getMaxEntriesLocalHeap());
      }

      return cacheManager;
    }
  }
}
