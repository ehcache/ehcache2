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
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class StorageStrategyNotSupportedTest extends AbstractCacheTestBase {

  public StorageStrategyNotSupportedTest(TestConfig testConfig) {
    super("ehcache-not-supported.xml", testConfig, App.class);
  }

  public static class App extends ClientBase {

    public App(String[] args) {
      super(args);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {

      CacheManager cm = getCacheManager();

      try {
        Assert.assertEquals(1, cm.getCacheNames().length);
        Assert.assertTrue(cm.getCacheNames()[0].equals("test"));
      } catch (CacheException e) {
        fail("Using storageStrategy=dcv2 should work even without ee");
      }

      // test programmatic way
      cm.shutdown();
      setupCacheManager();
      cm = getCacheManager();
      CacheConfiguration cacheConfiguration = new CacheConfiguration("testCache", 100);
      TerracottaConfiguration tc = new TerracottaConfiguration().clustered(true);
      cacheConfiguration.addTerracotta(tc);
      cache = new Cache(cacheConfiguration);

      cm.removeCache("test");

      try {
        cm.addCache(cache);
        Assert.assertEquals(1, cm.getCacheNames().length);
        Assert.assertTrue(cm.getCacheNames()[0].equals("testCache"));
      } catch (CacheException e) {
        e.printStackTrace();
        fail("Using storageStrategy=dcv2 should work even without ee");
      }
    }
  }
}
