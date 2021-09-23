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
package org.terracotta.ehcache.tests.servermap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.TerracottaConfiguration.Consistency;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class CacheSizeTest extends AbstractCacheTestBase {
  public CacheSizeTest(TestConfig testConfig) {
    super("/servermap/basic-servermap-test.xml", testConfig, CacheSizeTestClient.class);
  }

  public static class CacheSizeTestClient extends ClientBase {

    public CacheSizeTestClient(String[] args) {
      super("test", args);
    }

    public static void main(String[] args) {
      new CacheSizeTestClient(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      final int numElems = 5000;
      Assert.assertEquals(Consistency.STRONG, cache.getCacheConfiguration().getTerracottaConfiguration()
          .getConsistency());
      for (int i = 0; i < numElems; i++) {
        cache.put(new Element("key-" + i, "value-" + i));
      }
      System.out
          .println("Populated cache - strong. size: " + cache.getSize() + " keys size: " + cache.getKeys().size());

      Assert.assertEquals(numElems, cache.getSize());
      Assert.assertEquals("STRONG consistency cache: cache.getSize() should be equal to cache.getKeys().size()",
                          cache.getSize(), cache.getKeys().size());

      cache = cache.getCacheManager().getCache("eventualConsistencyCache");
      Assert.assertEquals(Consistency.EVENTUAL, cache.getCacheConfiguration().getTerracottaConfiguration()
          .getConsistency());
      for (int i = 0; i < numElems; i++) {
        cache.put(new Element("key-" + i, "value-" + i));
      }
      System.out.println("Populated cache - eventual. size: " + cache.getSize() + " keys size: "
                         + cache.getKeys().size());

      Assert.assertEquals(numElems, cache.getSize());
      Assert.assertEquals("EVENTUAL consistency cache: cache.getSize() should be equal to cache.getKeys().size()",
                          cache.getSize(), cache.getKeys().size());
    }

  }

}
