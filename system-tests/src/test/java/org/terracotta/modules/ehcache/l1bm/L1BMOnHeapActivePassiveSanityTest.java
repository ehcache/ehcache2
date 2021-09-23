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
package org.terracotta.modules.ehcache.l1bm;

import net.sf.ehcache.terracotta.AbstractTerracottaActivePassiveTestBase;

import com.tc.test.config.model.ServerCrashMode;
import com.tc.test.config.model.TestConfig;

public class L1BMOnHeapActivePassiveSanityTest extends AbstractTerracottaActivePassiveTestBase {

  public L1BMOnHeapActivePassiveSanityTest(TestConfig testConfig) {
    super(testConfig, L1BMOnHeapBasicSanityTestApp.class, L1BMOnHeapBasicSanityTestApp.class);
    testConfig.getL2Config().setMaxHeap(1024);
    testConfig.getL2Config().setOffHeapEnabled(true);
    testConfig.getL2Config().setMaxOffHeapDataSize(512);
    testConfig.addTcProperty("ehcache.evictor.logging.enabled", "true");
    testConfig.getCrashConfig().setCrashMode(ServerCrashMode.RANDOM_SERVER_CRASH);
    testConfig.getCrashConfig().setInitialDelayInSeconds(60);
    testConfig.getCrashConfig().setServerCrashWaitTimeInSec(30);
  }

}
