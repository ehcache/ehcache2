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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

public class TTICacheTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 3;

  public TTICacheTest(TestConfig testConfig) {
    super("tti-cache-test.xml", testConfig, App.class, App.class, App.class);
    disableTest();
  }

  public static class App extends ClientBase {

    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super(args);
      this.barrier = getClusteringToolkit().getBarrier("test", NODE_COUNT);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      testWithCache(false);
      testWithCache(true);
    }

    private void testWithCache(boolean statisticsEnabled) throws InterruptedException, BrokenBarrierException {
      final int index = barrier.await();

      Cache cache1 = cacheManager.getCache("test1"); // TTI = 10s
      Cache cache2 = cacheManager.getCache("test2"); // TTI = 20s
      cache1.removeAll();
      cache2.removeAll();
      barrier.await();

      if (index == 0) {
        cache1.put(new Element("key", "value"));
        cache2.put(new Element("key", "value"));
      }

      barrier.await();

      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      barrier.await();
      long expiryOne = cache1.getQuiet("key").getExpirationTime();
      while (System.currentTimeMillis() < expiryOne - TimeUnit.SECONDS.toMillis(8)) {
        Thread.sleep(100);
      }

      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());
      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());
      Assert.assertEquals("value", cache1.get("key").getObjectValue());
      Assert.assertEquals("value", cache2.get("key").getObjectValue());

      barrier.await();
      expiryOne = cache1.getQuiet("key").getExpirationTime();
      while (System.currentTimeMillis() < expiryOne + TimeUnit.SECONDS.toMillis(3)) {
        Thread.sleep(100);
      }

      // (key, value) in cache1 should expire
      Assert.assertNull(cache1.get("key"));

      // triggering last access time only on one node
      if (index == 0) {
        Assert.assertEquals("value", cache2.get("key").getObjectValue());
      }

      Thread.sleep(2000);

      barrier.await();

      Assert.assertNull(cache1.get("key"));

      // checking that other nodes received the last access time update and don't expire the element
      if (index != 1) {
        Assert.assertNotNull(cache2.get("key"));
      }

      barrier.await();
      long expiryTwo = cache2.getQuiet("key").getExpirationTime();
      while (System.currentTimeMillis() < expiryTwo + TimeUnit.SECONDS.toMillis(3)) {
        Thread.sleep(100);
      }

      // (key, value) in cache2 should expire
      Assert.assertNull(cache1.get("key"));
      Assert.assertNull(cache2.get("key"));

      cache1.put(new Element("key-1", "value-1"));
      // Check that last access time is being updated on each get.
      for (int i = 0; i < 10; i++) {
        TimeUnit.SECONDS.sleep(6); // Sleep for TTI/2 + 1
        Assert.assertNotNull(cache1.get("key-1"));
      }

      // Check that lastAccessTime is only Updated if time diff > TTI/2
      cache2.put(new Element("key-1", "value-1"));
      TimeUnit.SECONDS.sleep(5); // SLEEP for TTI/4
      Assert.assertNotNull(cache2.get("key-1"));
      TimeUnit.SECONDS.sleep(15); // SLEEP for 3/4 * TTI
      Assert.assertNull(cache2.get("key-1"));

    }

  }

}
