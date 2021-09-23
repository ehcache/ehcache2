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
package org.terracotta.ehcache.tests.container.hibernate.nontransactional;

import org.terracotta.ehcache.tests.container.hibernate.BaseClusteredRegionFactoryTest;

import com.tc.test.server.appserver.deployment.AbstractStandaloneTwoServerDeploymentTest;
import com.tc.test.server.appserver.deployment.DeploymentBuilder;

import javax.servlet.http.HttpServlet;

public abstract class NonTransactionalCacheTest extends BaseClusteredRegionFactoryTest {

  private static final String CONFIG_FILE_FOR_TEST = "hibernate-config/ehcache.xml";

  static class TestSetup extends BaseClusteredRegionFactoryTest.BaseClusteredCacheProviderTestSetup {

    private final Class<? extends HttpServlet> servletClass;

    TestSetup(Class<? extends AbstractStandaloneTwoServerDeploymentTest> testClass,
              Class<? extends HttpServlet> servletClass) {
      super(testClass, CONFIG_FILE_FOR_TEST);
      this.servletClass = servletClass;
    }

    TestSetup(Class<? extends AbstractStandaloneTwoServerDeploymentTest> testClass,
              Class<? extends HttpServlet> servletClass, String configFile) {
      super(testClass, configFile);
      this.servletClass = servletClass;
    }

    @Override
    protected void customizeWar(DeploymentBuilder builder) {
      builder.addResource("/hibernate-config/nontransactional/", "hibernate.cfg.xml",
                          "WEB-INF/classes/hibernate-config");
      builder.addResource("/hibernate-config/nontransactional/domain", "Item.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
      builder.addResource("/hibernate-config/nontransactional/domain", "Event.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
      builder.addResource("/hibernate-config/nontransactional/domain", "HolidayCalendar.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
      builder.addResource("/hibernate-config/nontransactional/domain", "Person.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
      builder.addResource("/hibernate-config/nontransactional/domain", "PhoneNumber.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
      builder.addResource("/hibernate-config/nontransactional/domain", "Account.hbm.xml",
                          "WEB-INF/classes/hibernate-config/domain");
    }

    @Override
    protected Class getServletClass() {
      return servletClass;
    }
  }

}
