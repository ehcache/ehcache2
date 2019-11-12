/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
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
