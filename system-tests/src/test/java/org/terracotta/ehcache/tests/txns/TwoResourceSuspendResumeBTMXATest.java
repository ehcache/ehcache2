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
import org.terracotta.test.util.TestBaseUtil;

import bitronix.tm.TransactionManagerServices;

import com.tc.test.config.model.TestConfig;

import java.util.ArrayList;
import java.util.List;

public class TwoResourceSuspendResumeBTMXATest extends AbstractCacheTestBase {

  public TwoResourceSuspendResumeBTMXATest(TestConfig testConfig) {
    super("two-resource-xa-test.xml", testConfig, TwoResourceSuspendResumeBTMClient.class);
    testConfig.getClientConfig().setParallelClients(false);
  }

  @Override
  protected List<String> getExtraJars() {
    List<String> extraJars = new ArrayList<String>();
    extraJars.add(TestBaseUtil.jarFor(TransactionManagerServices.class));
    return extraJars;
  }

}
