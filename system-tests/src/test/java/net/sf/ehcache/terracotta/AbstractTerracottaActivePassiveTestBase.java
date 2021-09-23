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
package net.sf.ehcache.terracotta;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.ServerCrashMode;
import com.tc.test.config.model.TestConfig;

public class AbstractTerracottaActivePassiveTestBase extends AbstractCacheTestBase {

  public AbstractTerracottaActivePassiveTestBase(TestConfig testConfig, Class<? extends ClientBase>... c) {
    this("basic-cache-test.xml", testConfig, c);
  }

  public AbstractTerracottaActivePassiveTestBase(String ehcacheConfigPath, TestConfig testConfig,
                                                 Class<? extends ClientBase>... c) {
    super(ehcacheConfigPath, testConfig, c);

    testConfig.setRestartable(false);

    testConfig.getGroupConfig().setMemberCount(2);

    testConfig.getCrashConfig().setCrashMode(ServerCrashMode.NO_CRASH);
  }

}
