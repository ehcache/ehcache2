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

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class CacheTierPinningTest extends AbstractCacheTestBase {

  private static final int ELEMENT_COUNT = 1000;

  public CacheTierPinningTest(TestConfig testConfig) {
    super("cache-pinning-test.xml", testConfig, App.class);
  }

  public static class App extends ClientBase {
    public App(String[] args) {
      super("pinned", args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {

      System.out.println("Testing with Strong tier pinned cache");
      runBasicPinningTest(cacheManager.getCache("pinned"));
      System.out.println("Testing with Eventual tier pinned cache");
      runBasicPinningTest(cacheManager.getCache("pinnedEventual"));
    }

    private void runBasicPinningTest(Cache cache) {
      for (int i = 0; i < ELEMENT_COUNT; i++) {
        cache.put(new Element(i, i));
      }

      Assert.assertEquals(ELEMENT_COUNT, cache.getSize());

      for (int i = 0; i < ELEMENT_COUNT; i++) {
        Assert.assertNotNull(cache.get(i));
      }

      Assert.assertEquals(ELEMENT_COUNT, cache.getStatistics().localHeapHitCount());
//      Assert.assertEquals(0, cache.getStatistics().remoteHitCount());
//      Assert.assertEquals(0, cache.getStatistics().remoteMissCount());
      Assert.assertEquals(0, cache.getStatistics().cacheEvictedCount());
    }

  }
}
