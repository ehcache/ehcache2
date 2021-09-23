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

public class BasicStandaloneCacheAndServerTopologyTest extends AbstractCacheTestBase {

  int dsoPort;

  public BasicStandaloneCacheAndServerTopologyTest(TestConfig testConfig) {
    super("basic-cache-test-different-server-topology.xml", testConfig);
    testConfig.getClientConfig().setParallelClients(false);
  }

  @Override
  protected void startClients() throws Throwable {
    getTestConfig().getClientConfig().addExtraClientJvmArg("-Dmy.tc.server.topology=127.0.0.1:"
                                                               + getGroupData(0).getTsaPort(0));
    getTestConfig().getClientConfig().addExtraClientJvmArg("-Dtc.server.topology=127.0.0.1:"
                                                               + getGroupData(0).getTsaPort(0));

    runClient(Client3.class);
    runClient(Client4.class);
  }
}
