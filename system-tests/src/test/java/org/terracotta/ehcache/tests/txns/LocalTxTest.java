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
package org.terracotta.ehcache.tests.txns;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;

import com.tc.test.config.model.ClientConfig;
import com.tc.test.config.model.TestConfig;

public class LocalTxTest extends AbstractCacheTestBase {

  public LocalTxTest(TestConfig testConfig) {
    super("local-tx-test.xml", testConfig, LocalTxClient.class);
    ClientConfig clientConfig = testConfig.getClientConfig();
    clientConfig.setParallelClients(true);
    clientConfig.getBytemanConfig().setScript("/byteman/LocalTxTestDebug.btm");
  }

}
