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

import org.terracotta.ehcache.tests.AbstractCacheTestBase;

import com.tc.test.config.model.TestConfig;

public class ServerMapL2EvictionReachesOneL1Test extends AbstractCacheTestBase {

  public ServerMapL2EvictionReachesOneL1Test(TestConfig testConfig) {
    super("/servermap/servermap-l2-eviction-reaches-one-l1-test.xml", testConfig,
          ServerMapL2EvictionReachesOneL1TestClient.class, ServerMapL2EvictionReachesOneL1Verifier.class);
    testConfig.setDgcEnabled(true);
    testConfig.setDgcIntervalInSec(60);
    testConfig.addTcProperty("ehcache.evictor.logging.enabled", "true");

    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.l1.cachemanager.enabled=false");
    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.ehcache.evictor.logging.enabled=true");
    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.l1.lockmanager.timeout.interval=60000");
  }

}
