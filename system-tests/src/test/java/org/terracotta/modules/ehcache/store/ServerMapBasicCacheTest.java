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

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class ServerMapBasicCacheTest extends AbstractCacheTestBase {

  public ServerMapBasicCacheTest(TestConfig testConfig) {
    super("server-map-cache-test.xml", testConfig, ServerMapBasicCacheApp.class, ServerMapBasicCacheApp.class);
  }

  public static class ServerMapBasicCacheApp extends ClientBase {
    private static final int NODE_COUNT = 2;
    private final ToolkitBarrier    barrier;

    public ServerMapBasicCacheApp(String[] args) {
      super("defaultStorageStrategyCache", args);
      this.barrier = getClusteringToolkit().getBarrier("test", NODE_COUNT);
    }

    public static void main(String[] args) {
      new ServerMapBasicCacheApp(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = this.barrier.await();

      cache = cacheManager.getCache("test");

      // XXX: assert that the cache is clustered via methods on cache
      // config (when methods exist)

      Assert.assertEquals(0, cache.getSize());

      this.barrier.await();

      if (index == 0) {
        cache.put(new Element("key", "value"));
        waitForAllCurrentTransactionsToComplete(cache);
      }

      this.barrier.await();

      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", cache.get("key").getObjectValue());
      System.err.println("Index : " + index + " : Cache size : " + cache.getSize());

      this.barrier.await();

      if (index == 0) {
        final boolean removed = cache.remove("key");
        Assert.assertTrue(removed);
        waitForAllCurrentTransactionsToComplete(cache);
      }

      this.barrier.await();

      Assert.assertEquals(0, cache.getSize());
    }
  }
}
