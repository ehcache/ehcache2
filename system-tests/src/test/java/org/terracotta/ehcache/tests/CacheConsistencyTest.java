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
package org.terracotta.ehcache.tests;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.TerracottaConfiguration.Consistency;

import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class CacheConsistencyTest extends AbstractCacheTestBase {

  public CacheConsistencyTest(TestConfig testConfig) {
    super("cache-consistency-test.xml", testConfig, CacheConsistencyTestClient.class);
  }

  public static class CacheConsistencyTestClient extends ClientBase {

    public CacheConsistencyTestClient(String[] args) {
      super("strongConsistencyCache", args);
    }

    public static void main(String[] args) {
      new CacheConsistencyTestClient(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      Cache strongConsistencyCache = cache.getCacheManager().getCache("strongConsistencyCache");
      Cache eventualConsistencyCache = cache.getCacheManager().getCache("eventualConsistencyCache");

      assertConsistency(strongConsistencyCache, eventualConsistencyCache);

      for (int i = 0; i < 5; i++) {
        System.out.println("Turning on bulk-load - " + i);
        strongConsistencyCache.setNodeBulkLoadEnabled(true);
        eventualConsistencyCache.setNodeBulkLoadEnabled(true);
        assertConsistency(strongConsistencyCache, eventualConsistencyCache);

        System.out.println("Turning off bulk-load - " + i);
        strongConsistencyCache.setNodeBulkLoadEnabled(false);
        strongConsistencyCache.waitUntilClusterBulkLoadComplete();
        eventualConsistencyCache.setNodeBulkLoadEnabled(false);
        eventualConsistencyCache.waitUntilClusterBulkLoadComplete();
        assertConsistency(strongConsistencyCache, eventualConsistencyCache);
      }
    }

    private void assertConsistency(Cache strongConsistencyCache, Cache eventualConsistencyCache) {
      System.out.println("Asserting consistency");
      Assert.assertEquals(Consistency.STRONG, strongConsistencyCache.getCacheConfiguration()
          .getTerracottaConfiguration().getConsistency());
      Assert.assertEquals(Consistency.EVENTUAL, eventualConsistencyCache.getCacheConfiguration()
          .getTerracottaConfiguration().getConsistency());
    }
  }

}
