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

import com.tc.test.config.model.TestConfig;

public class UrlConfigStandaloneCacheTest extends AbstractCacheTestBase {

  public UrlConfigStandaloneCacheTest(TestConfig testConfig) {
    super("url-config-cache-test.xml", testConfig);
  }

  @Override
  protected void runClient(Class client) throws Throwable {
    writeXmlFileWithPort("url-config-cache-test-tc-config.xml", "tc-config-client.xml");
    super.runClient(client);
  }
}
