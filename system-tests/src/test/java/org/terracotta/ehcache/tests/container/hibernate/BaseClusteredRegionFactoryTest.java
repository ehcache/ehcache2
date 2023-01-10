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
package org.terracotta.ehcache.tests.container.hibernate;

import antlr.Tool;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Layout;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.StandardAppServerParameters;
import com.tc.test.server.appserver.deployment.AbstractStandaloneTwoServerDeploymentTest;
import com.tc.test.server.appserver.deployment.DeploymentBuilder;
import com.tc.test.server.appserver.deployment.WebApplicationServer;
import com.tc.util.PortChooser;
import javassist.util.proxy.ProxyFactory;
import net.sf.ehcache.util.DerbyWrapper;
import org.apache.commons.collections.LRUMap;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.client.ClientDataSourceFactory;
import org.apache.derby.drda.NetServlet;
import org.apache.derby.iapi.tools.ToolUtils;
import org.apache.derby.impl.db.BasicDatabase;
import org.dom4j.Node;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.common.reflection.MetadataProvider;
import org.terracotta.ehcache.tests.container.ContainerTestSetup;
import org.terracotta.ehcache.tests.container.hibernate.nontransactional.HibernateUtil;

import javax.persistence.EntityListeners;
import javax.transaction.Synchronization;
import java.io.File;

public abstract class BaseClusteredRegionFactoryTest extends AbstractStandaloneTwoServerDeploymentTest {

  public void testHibernateCacheProvider() throws Exception {
    WebClient conversation = new WebClient();

    WebResponse response1 = hibernateRequest(server0, "server=server0", conversation);
    assertEquals("OK", response1.getContentAsString().trim());

    WebResponse response2 = hibernateRequest(server1, "server=server1", conversation);
    assertEquals("OK", response2.getContentAsString().trim());
  }

  private WebResponse hibernateRequest(WebApplicationServer server, String params, WebClient con) throws Exception {
    return server.ping("/test/HibernateCacheTestServlet?" + params, con);
  }

  public static abstract class BaseClusteredCacheProviderTestSetup extends ContainerTestSetup {

    private DerbyWrapper derbyWrapper;
    private final Class  testClass;
    private final int    derbyPort = new PortChooser().chooseRandomPort();

    protected BaseClusteredCacheProviderTestSetup(Class<? extends AbstractStandaloneTwoServerDeploymentTest> testClass,
                                                  String ehcacheConfigFile) {
      super(testClass, ehcacheConfigFile, "test");
      this.testClass = testClass;
    }

    @Override
    protected final void configureWar(DeploymentBuilder builder) {
      super.configureWar(builder);
      builder.addDirectoryOrJARContainingClass(com.tc.test.TCTestCase.class); // hibernate*.jar
      builder.addDirectoryOrJARContainingClass(SessionFactory.class); // hibernate*.jar
      builder.addDirectoryOrJARContainingClass(MetadataProvider.class); // hibernate*.jar
      builder.addDirectoryOrJARContainingClass(EntityListeners.class); // JPA
      builder.addDirectoryOrJARContainingClass(LRUMap.class);
      builder.addDirectoryOrJARContainingClass(BasicDatabase.class); // derby
      builder.addDirectoryOrJARContainingClass(ToolUtils.class); // derbytools
      builder.addDirectoryOrJARContainingClass(NetServlet.class); // derbynet
      builder.addDirectoryOrJARContainingClass(ClientDataSourceFactory.class); // derbyclient
      builder.addDirectoryOrJARContainingClass(Node.class); // domj4*.jar
      builder.addDirectoryOrJARContainingClass(Tool.class); // antlr*.jar
      builder.addDirectoryOrJARContainingClass(ProxyFactory.class); // java-assist

      // Tomcat is not a full J2EE application-server - we have to manually add the JTA classes to its classpath.
      if (appServerInfo().getId() == AppServerInfo.TOMCAT || appServerInfo().getId() == AppServerInfo.JETTY) {
        builder.addDirectoryOrJARContainingClass(Synchronization.class); // jta
      }

      if (appServerInfo().getId() != AppServerInfo.JBOSS) {
        builder.addDirectoryOrJARContainingClass(LoggerContext.class);
        builder.addDirectoryOrJARContainingClass(Layout.class); // logback
        builder.addDirectoryOrJARContainingClass(LogFactory.class); // common-loggings
      }
      if (appServerInfo().getId() == AppServerInfo.JBOSS && !appServerInfo().getName().equals("jboss-eap")) {
        builder.addResource("/hibernate-config/appserver/", "jboss-web.xml", "WEB-INF");
      }
      builder.addResource("/hibernate-config/appserver/", "weblogic.xml", "WEB-INF");

      customizeWar(builder);

      builder.addServlet("HibernateCacheTestServlet", "/HibernateCacheTestServlet/*", getServletClass(), null, false);
    }

    @Override
    protected void configureServerParamers(StandardAppServerParameters params) {
      super.configureServerParamers(params);
      params.appendSysProp(HibernateUtil.DB_PORT_SYSPROP, derbyPort);
    }

    @Override
    public final void setUp() throws Exception {
      // To debug servlets:
      // System.setProperty("com.tc.test.server.appserver.deployment.GenericServer.ENABLE_DEBUGGER", "true");
      File derbyWorkDir = new File("derbydb", testClass.getSimpleName() + "-" + System.currentTimeMillis());
      if (!derbyWorkDir.exists() && !derbyWorkDir.mkdirs()) { throw new RuntimeException(
                                                                                         "Can't create derby work dir "
                                                                                             + derbyWorkDir
                                                                                                 .getAbsolutePath()); }

      derbyWrapper = new DerbyWrapper(derbyPort, derbyWorkDir.getCanonicalPath());
      derbyWrapper.start();
      super.setUp();
    }

    @Override
    public final void tearDown() throws Exception {
      super.tearDown();
      if (derbyWrapper != null) {
        derbyWrapper.stop();
      }
    }

    protected boolean isConfiguredToRunWithAppServer() {
      return !"unknown".equals(appServerInfo().getName());
    }

    protected abstract void customizeWar(DeploymentBuilder builder);

    protected abstract Class getServletClass();
  }
}
