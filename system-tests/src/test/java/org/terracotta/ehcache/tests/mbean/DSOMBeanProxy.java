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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

/**
 * @author Abhishek Sanoujam
 */
public class DSOMBeanProxy extends MBeanServerInvocationHandler {

  public DSOMBeanProxy(MBeanServerConnection mbs, ObjectName objectName) {
    super(mbs, objectName);
  }

  public static DSOMBean newL2ControlMBeanProxy(MBeanServerConnection connection, ObjectName objectName) {
    final InvocationHandler handler = new DSOMBeanProxy(connection, objectName);
    final Class[] interfaces = { DSOMBean.class };
    Object proxy = Proxy.newProxyInstance(DSOMBean.class.getClassLoader(), interfaces, handler);
    return DSOMBean.class.cast(proxy);
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return super.invoke(proxy, method, args);
  }

}
