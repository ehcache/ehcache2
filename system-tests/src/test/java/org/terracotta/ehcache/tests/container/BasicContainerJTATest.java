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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.deployment.AbstractStandaloneTwoServerDeploymentTest;
import com.tc.test.server.appserver.deployment.DeploymentBuilder;
import com.tc.test.server.appserver.deployment.WebApplicationServer;

import junit.framework.Test;

public class BasicContainerJTATest extends AbstractStandaloneTwoServerDeploymentTest {

  private static final String CONTEXT = "BasicContainerJTATest";

  public BasicContainerJTATest() {
    if (appServerInfo().getId() == AppServerInfo.JETTY || appServerInfo().getId() == AppServerInfo.TOMCAT
        || appServerInfo().getId() == AppServerInfo.WEBSPHERE) {
      // Jetty and Tomcat have no TM and we know the Websphere one is not compatible
      disableTest();
    }
  }

  public static Test suite() {
    return new BasicContainerJTATestSetup();
  }

  public void testBasics() throws Exception {
    System.out.println("Running test");
    WebClient conversation = new WebClient();

    // do insert on server0
    WebResponse response1 = request(server0, "cmd=insert", conversation);
    assertEquals("OK", response1.getContentAsString().trim());

    // do query on server1
    response1 = request(server1, "cmd=query", conversation);
    assertEquals("OK", response1.getContentAsString().trim());
    System.out.println("Test finished");
  }

  private WebResponse request(WebApplicationServer server, String params, WebClient con) throws Exception {
    return server.ping("/" + CONTEXT + "/BasicJTATestServlet?" + params, con);
  }

  private static class BasicContainerJTATestSetup extends AbstractStandaloneContainerJTATestSetup {

    public BasicContainerJTATestSetup() {
      super(BasicContainerJTATest.class, "basic-xa-appserver-test.xml", CONTEXT);
    }

    @Override
    protected void configureWar(DeploymentBuilder builder) {
      super.configureWar(builder);
      builder.addServlet("BasicTestJTAServlet", "/BasicJTATestServlet/*", BasicJTATestServlet.class, null, false);
    }

  }

}
