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
package org.terracotta.ehcache.tests.container;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Layout;

import com.tc.test.server.appserver.deployment.AbstractStandaloneTwoServerDeploymentTest;
import com.tc.test.server.appserver.deployment.DeploymentBuilder;

public class AbstractStandaloneContainerJTATestSetup extends ContainerTestSetup {

  public AbstractStandaloneContainerJTATestSetup(Class<? extends AbstractStandaloneTwoServerDeploymentTest> testClass,
                                                 String ehcacheConfigTemplate, String context) {
    super(testClass, ehcacheConfigTemplate, context);
  }

  @Override
  protected void addCommonJars(DeploymentBuilder builder) {
    super.addCommonJars(builder);
    builder.addDirectoryOrJARContainingClass(LoggerContext.class);
    builder.addDirectoryOrJARContainingClass(Layout.class);
  }

}
