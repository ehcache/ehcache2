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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.ehcache.tests.mbean.DSOMBeanController;

public class ServerMapClearExpressTestClient1 extends ServerMapClientBase {

  private final DSOMBeanController dsoMBean;

  public ServerMapClearExpressTestClient1(String[] args) {
    super("test", args);
    dsoMBean = new DSOMBeanController("localhost", getTestControlMbean().getGroupsData()[0].getJmxPort(0));
  }

  public static void main(String[] args) {
    new ServerMapClearExpressTestClient1(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    ServerMapClearTestHelper.doTest(cache, clusteringToolkit, dsoMBean);
  }

}
