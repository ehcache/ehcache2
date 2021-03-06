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
package org.terracotta.modules.ehcache.coherence;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.test.util.JMXUtils;
import org.terracotta.test.util.TestBaseUtil;
import org.terracotta.toolkit.Toolkit;

import com.tc.management.TerracottaMBean;
import com.tc.management.beans.L2MBeanNames;
import com.tc.object.locks.LockID;
import com.tc.objectserver.locks.LockMBean;
import com.tc.objectserver.storage.api.OffheapStats;
import com.tc.properties.TCPropertiesConsts;
import com.tc.stats.api.DSOMBean;
import com.tc.test.config.model.TestConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.remote.JMXConnector;

import junit.framework.Assert;

/**
 * @author Abhishek Sanoujam
 */
public class NoLocksCreatedEventualTest extends AbstractCacheTestBase {

  public NoLocksCreatedEventualTest(TestConfig testConfig) {
    super("cache-coherence-test.xml", testConfig, NoLocksCreatedEventualTestClient.class);
    testConfig.addTcProperty(TCPropertiesConsts.L1_LOCKMANAGER_TIMEOUT_INTERVAL, "9000000");

  }

  @Override
  protected List<String> getExtraJars() {
    List<String> jars = new ArrayList<String>(super.getExtraJars());
    jars.add(TestBaseUtil.jarFor(TerracottaMBean.class));
    jars.add(TestBaseUtil.jarFor(DSOMBean.class));
    jars.add(TestBaseUtil.jarFor(OffheapStats.class));
    jars.add(TestBaseUtil.jarFor(LockID.class));
    jars.add(TestBaseUtil.jarFor(com.tc.object.locks.ServerLockContext.State.class));
    return jars;
  }

  public static class NoLocksCreatedEventualTestClient extends ClientBase {

    public NoLocksCreatedEventualTestClient(String[] args) {
      super("non-strict-Cache", args);

    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      DSOClientMBeanCoordinator coordinator = new DSOClientMBeanCoordinator();
      coordinator.startDSOClientMBeanCoordinator();
      // call a put before calculating initial locks. So that internal cache gets initialized.
      cache.put(new Element("key", "value"));
      int initialLocks = coordinator.getLocks().size();
      for (int i = 0; i < 1000; i++) {
        cache.put(new Element("key" + i, "value" + i));
      }

      for (int i = 0; i < 5000; i++) {
        Element element = cache.get("key" + (i % 1000));
        Assert.assertNotNull(element);
        Assert.assertEquals("key" + (i % 1000), element.getKey());
        Assert.assertEquals("value" + (i % 1000), element.getValue());
      }

      Assert.assertEquals("No lock should have been created ", 0, (coordinator.getLocks().size() - initialLocks));

      for (int i = 0; i < 100; i++) {
        cache.acquireReadLockOnKey("key" + i);
      }
      Assert.assertEquals(100, (coordinator.getLocks().size() - initialLocks));

      for (int i = 100; i < 200; i++) {
        cache.acquireWriteLockOnKey("key" + i);
      }
      Assert.assertEquals(200, (coordinator.getLocks().size() - initialLocks));

      for (int i = 0; i < 5000; i++) {
        Element element = cache.get("key" + (i % 1000));
        Assert.assertNotNull(element);
        Assert.assertEquals("key" + (i % 1000), element.getKey());
        Assert.assertEquals("value" + (i % 1000), element.getValue());
      }

      Assert.assertEquals(1000, (coordinator.getLocks().size() - initialLocks));

    }

    private class DSOClientMBeanCoordinator {

      private DSOMBean              dsoMBean;
      private MBeanServerConnection mbsc;

      public void startDSOClientMBeanCoordinator() {
        try {
          JMXConnector jmxc = JMXUtils.getJMXConnector("localhost",
                                                       getTestControlMbean().getGroupsData()[0].getJmxPort(0));
          mbsc = jmxc.getMBeanServerConnection();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        dsoMBean = MBeanServerInvocationHandler.newProxyInstance(mbsc, L2MBeanNames.DSO, DSOMBean.class, false);
      }

      public List<LockID> getLocks() {
        ArrayList<LockID> rv = new ArrayList<LockID>();
        LockMBean[] locks = dsoMBean.getLocks();
        for (LockMBean lockMBean : locks) {
          rv.add(lockMBean.getLockID());
          System.out.println(lockMBean.getLockID());
        }
        return rv;
      }
    }
  }

}
