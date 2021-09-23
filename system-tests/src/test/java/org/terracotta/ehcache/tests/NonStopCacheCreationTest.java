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
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;
import com.tc.test.config.model.TestConfig;

public class NonStopCacheCreationTest extends AbstractCacheTestBase {

  private static final String XML = "/ehcache-config.xml";

  public NonStopCacheCreationTest(TestConfig testConfig) {
    super("/non-stop-cache-creation-test.xml", testConfig, NonStopCacheTestClient.class);
  }

  public static class NonStopCacheTestClient extends ClientBase {

    public NonStopCacheTestClient(String[] args) {
      super("test", args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      cache.getCacheManager().shutdown();
      
      CacheManager cm = new CacheManager(ClientBase.class.getResourceAsStream(XML));
      testCacheManager(cm);
      cm.shutdown();

      cm = CacheManager.create(ClientBase.class.getResourceAsStream(XML));
      testCacheManager(cm);
      cm.shutdown();
    }

    private void testCacheManager(CacheManager cm) {
      Cache c = cm.getCache("test");
      c.put(new Element("foo", "bar"));
      assertEquals("bar", c.get("foo").getObjectValue());
      c.remove("foo");
      assertNull(c.get("foo"));
    }

  }
}
