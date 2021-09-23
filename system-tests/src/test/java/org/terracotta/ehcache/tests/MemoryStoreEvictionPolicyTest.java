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
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

public class MemoryStoreEvictionPolicyTest extends AbstractCacheTestBase {
  public MemoryStoreEvictionPolicyTest(TestConfig testConfig) {
    super("memory-store-eviction-policy-test.xml", testConfig, MemoryStoreEvictionPolicyTestApp.class);
  }

  public static class MemoryStoreEvictionPolicyTestApp extends ClientBase {
    public static void main(String[] args) {
      new MemoryStoreEvictionPolicyTestApp(args).run();
    }

    public MemoryStoreEvictionPolicyTestApp(String[] args) {
      super("test", args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      Cache fifo = new Cache(new CacheConfiguration("fifo", 1000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.FIFO).terracotta(new TerracottaConfiguration()));
      try {
        cacheManager.addCache(fifo);
        fail();
      } catch (InvalidConfigurationException e) {
        // expected exception
      }

      Cache lfu = new Cache(new CacheConfiguration("lfu", 1000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU).terracotta(new TerracottaConfiguration()));
      try {
        cacheManager.addCache(lfu);
        fail();
      } catch (InvalidConfigurationException e) {
        // expected exception
      }

      Cache lru = new Cache(new CacheConfiguration("lru", 1000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU).terracotta(new TerracottaConfiguration()));
      cacheManager.addCache(lru);

      Cache clock = new Cache(new CacheConfiguration("clock", 1000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.CLOCK).terracotta(new TerracottaConfiguration()));
      cacheManager.addCache(clock);

      Cache none = new Cache(new CacheConfiguration("none", 1000).terracotta(new TerracottaConfiguration()));
      cacheManager.addCache(none);

      none.getSize();
    }
  }
}
