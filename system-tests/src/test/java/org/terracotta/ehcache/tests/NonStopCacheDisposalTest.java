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
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

public class NonStopCacheDisposalTest extends AbstractCacheTestBase {

  public NonStopCacheDisposalTest(TestConfig testConfig) {
    super("/non-stop-cache-disposal-test.xml", testConfig, NonStopCacheTestClient.class);
    testConfig.getClientConfig().getBytemanConfig().setScript("/byteman/nonStopCacheDisposal.btm");
  }

  public static class NonStopCacheTestClient extends ClientBase {

    public NonStopCacheTestClient(String[] args) {
      super("test", args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      // first make sure NonStop does interrupt cache operations
      try {
        cache.put(new Element(1, 1));
        fail("expected NonStopCacheException");
      } catch (NonStopCacheException nse) {
        // expected
      }

      // then make sure NonStop does not interrupt cache manager shutdown
      cache.getCacheManager().shutdown();
    }

  }
}
