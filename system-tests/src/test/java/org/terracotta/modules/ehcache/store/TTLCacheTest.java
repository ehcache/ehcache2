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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

public class TTLCacheTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 3;

  public TTLCacheTest(TestConfig testConfig) {
    super("ttl-cache-test.xml", testConfig, App.class, App.class, App.class);
  }

  public static class App extends ClientBase {

    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super("test1", args);
      this.barrier = getClusteringToolkit().getBarrier("test", NODE_COUNT);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      Cache cache1 = cacheManager.getCache("test1");
      Cache cache2 = cacheManager.getCache("test2");

      barrier.await();

      if (index == 0) {
        cache1.put(new Element("key", "value"));
        cache2.put(new Element("key", "value"));
      }

      barrier.await();
      long expiryOne = cache1.get("key").getExpirationTime();
      long expiryTwo = cache2.get("key").getExpirationTime();

      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      while (System.currentTimeMillis() < expiryOne - TimeUnit.SECONDS.toMillis(5)) {
        Thread.sleep(100);
      }

      barrier.await();

      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      while (System.currentTimeMillis() < expiryOne + TimeUnit.SECONDS.toMillis(1)) {
        Thread.sleep(100);
      }

      barrier.await();

      Assert.assertNull(cache1.get("key"));
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      while (System.currentTimeMillis() < expiryTwo - TimeUnit.SECONDS.toMillis(10)) {
        Thread.sleep(100);
      }

      barrier.await();

      Assert.assertNull(cache1.get("key"));
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      while (System.currentTimeMillis() < expiryTwo + TimeUnit.SECONDS.toMillis(1)) {
        Thread.sleep(100);
      }

      barrier.await();

      Assert.assertNull(cache1.get("key"));
      Assert.assertNull(cache2.get("key"));
    }

  }

}
