/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package net.sf.ehcache.management.service.impl;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.l1bridge.AbstractRemoteAgentEndpointImpl;

import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;
import java.util.Map;


public class RemoteAgentEndpointImpl extends AbstractRemoteAgentEndpointImpl implements RemoteAgentEndpointImplMBean {
  private static final Logger LOG = LoggerFactory.getLogger(RemoteAgentEndpointImpl.class);

  public static final String AGENCY = "Ehcache";
  public static final String MBEAN_NAME_PREFIX = "net.sf.ehcache:type=" + IDENTIFIER + ",agency=" + AGENCY;

  private final ThreadLocal<String> requestClusterUUID = new ThreadLocal<String>();

  private final Map<String, ObjectName> objectNames = new ConcurrentHashMap<String, ObjectName>();

  public RemoteAgentEndpointImpl() {
  }

  protected boolean isTsaSecured() {
    return false;
  }

  public String getRequestClusterUUID() {
    return requestClusterUUID.get();
  }

  public boolean isTsaBridged() {
    return getRequestClusterUUID() != null;
  }

  public void registerMBean(final String clientUUID) {
    if (clientUUID == null) {
      throw new NullPointerException("clientUUID cannot be null");
    }

    ObjectName objectName;
    try {
      objectName = new ObjectName(MBEAN_NAME_PREFIX + ",node=" + clientUUID);
      MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
      platformMBeanServer.registerMBean(new StandardMBean(this, RemoteAgentEndpointImplMBean.class) {
        @Override
        public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
          try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
              // because some code in Jersey is using the TCCL to resolve some classes
              Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
              requestClusterUUID.set(clientUUID);
              return super.invoke(actionName, params, signature);
            } finally {
              Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
          } finally {
            requestClusterUUID.remove();
          }
        }
      }, objectName);
    } catch (Exception e) {
      LOG.warn("Error registering RemoteAgentEndpointImpl MBean with UUID: " + clientUUID, e);
      objectName = null;
    }
    this.objectNames.put(clientUUID, objectName);
  }

  public void unregisterMBean(String clientUUID) {
    ObjectName objectName = objectNames.remove(clientUUID);
    if (objectName == null) {
      return;
    }
    try {
      MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
      platformMBeanServer.unregisterMBean(objectName);
    } catch (Exception e) {
      LOG.warn("Error unregistering RemoteAgentEndpointImpl MBean : " + objectName, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getVersion() {
    return this.getClass().getPackage().getImplementationVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAgency() {
    return AGENCY;
  }

}
