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
package org.terracotta.ehcache.tests.mbean;

import org.terracotta.test.util.JMXUtils;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

/**
 * @author Abhishek Sanoujam
 */
public class DSOMBeanController implements DSOMBean {

  private static final ObjectName DSO;
  private static final ObjectName DUMPER;
  static {
    ObjectName dso;
    ObjectName dumper = null;
    try {
      dso = new ObjectName("org.terracotta:type=Terracotta Server,name=DSO");
      dumper = new ObjectName("org.terracotta.internal:type=Terracotta Server,name=L2Dumper");
    } catch (Exception e) {
      dso = null;
      dumper = null;
    }
    DSO = dso;
    DUMPER = dumper;
  }

  private final String            host;
  private final int               jmxPort;

  public DSOMBeanController(String host, int jmxPort) {
    this.host = host;
    this.jmxPort = jmxPort;
  }

  private <T> T performL2MBeanDumper(DSOMBeanAction<T> action) throws IOException {
    return performL2ControlBeanAction(action, DUMPER);
  }

  private <T> T performL2MBeanTCServerInfo(DSOMBeanAction<T> action) throws IOException {
    return performL2ControlBeanAction(action, DSO);
  }

  private <T> T performL2ControlBeanAction(DSOMBeanAction<T> action, ObjectName objectName) throws IOException {
    if (objectName == null) { throw new RuntimeException(objectName + " object name is null"); }
    final JMXConnector jmxConnector = JMXUtils.getJMXConnector(host, jmxPort);
    MBeanServerConnection mbs = jmxConnector.getMBeanServerConnection();
    DSOMBean l2ControlBean = DSOMBeanProxy.newL2ControlMBeanProxy(mbs, objectName);
    try {
      return action.performL2ControlBeanAction(l2ControlBean);
    } finally {
      try {
        jmxConnector.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public long getGlobalServerMapGetSizeRequestsCount() {

    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Long>() {

        public Long performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getGlobalServerMapGetSizeRequestsCount();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getGlobalServerMapGetSizeRequestsRate() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Long>() {

        public Long performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getGlobalServerMapGetSizeRequestsRate();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getGlobalServerMapGetValueRequestsCount() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Long>() {

        public Long performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getGlobalServerMapGetValueRequestsCount();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getGlobalServerMapGetValueRequestsRate() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Long>() {

        public Long performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getGlobalServerMapGetValueRequestsRate();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getReadOperationRate() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Long>() {

        public Long performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getReadOperationRate();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<ObjectName, Long> getServerMapGetSizeRequestsCount() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Map<ObjectName, Long>>() {

        public Map<ObjectName, Long> performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getServerMapGetSizeRequestsCount();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<ObjectName, Long> getServerMapGetSizeRequestsRate() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Map<ObjectName, Long>>() {

        public Map<ObjectName, Long> performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getServerMapGetSizeRequestsRate();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<ObjectName, Long> getServerMapGetValueRequestsCount() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Map<ObjectName, Long>>() {

        public Map<ObjectName, Long> performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getServerMapGetValueRequestsCount();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<ObjectName, Long> getServerMapGetValueRequestsRate() {
    try {
      return performL2MBeanTCServerInfo(new DSOMBeanAction<Map<ObjectName, Long>>() {

        public Map<ObjectName, Long> performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.getServerMapGetValueRequestsRate();
        }

      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static interface DSOMBeanAction<T> {
    T performL2ControlBeanAction(DSOMBean l2ControlBean);
  }

  public Void dumpClusterState() {
    try {
      return performL2MBeanDumper(new DSOMBeanAction<Void>() {

        public Void performL2ControlBeanAction(DSOMBean dsoMBean) {
          return dsoMBean.dumpClusterState();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
