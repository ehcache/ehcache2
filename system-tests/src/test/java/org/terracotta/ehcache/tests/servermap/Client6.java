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

import org.terracotta.toolkit.Toolkit;

public class Client6 extends ServerMapClientBase {

  public Client6(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new Client6(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    assertClient1Exited(cache);
    BasicServerMapExpressTestHelper.assertValuesInCache(cache);
  }

  private void assertClient1Exited(Cache cache) {
    Element element = cache.get("client1-exited");
    if (element == null) { throw new AssertionError("Element should not be null"); }
    if (!"true".equals(element.getObjectValue())) {
      //
      throw new AssertionError("Client1 should have already exited before this");
    }
  }
}
