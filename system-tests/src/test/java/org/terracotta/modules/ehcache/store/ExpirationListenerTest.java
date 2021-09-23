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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.test.util.WaitUtil;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

public class ExpirationListenerTest extends AbstractCacheTestBase {

  public ExpirationListenerTest(TestConfig testConfig) {
    super("expire-cache-test.xml", testConfig, App.class);
  }

  public static class App extends ClientBase implements CacheEventListener {

    private final AtomicLong localExpiredCount = new AtomicLong();

    public App(String[] args) {
      super(args);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(final Cache cache, final Toolkit clusteringToolkit) throws Throwable {
      cache.getCacheEventNotificationService().registerListener(this);
      // XXX: assert that the cache is clustered via methods on cache config (when methods exist)
      Assert.assertEquals(0, cache.getSize());
      cache.put(new Element("key", "value"));

      // make sure the item has been evicted
      WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          return cache.getSize() == 0 && localExpiredCount.get() > 0;
        }
      });

      // To make sure L2 evicts the entry
      Assert.assertNull(cache.get("key"));
      // only assert local listener would notice eviction events
      Assert.assertEquals(1, localExpiredCount.get());
      Assert.assertEquals(0, cache.getSize());
    }

    public void dispose() {
      // don't care
    }

    public void notifyElementEvicted(Ehcache cache, Element element) {
      System.out.println("Element [" + element + "] evicted");
    }

    public void notifyElementExpired(Ehcache cache, Element element) {
      localExpiredCount.incrementAndGet();
    }

    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
      // don't care
    }

    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
      // don't care
    }

    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
      // don't care
    }

    public void notifyRemoveAll(Ehcache cache) {
      // don't care
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }

}
