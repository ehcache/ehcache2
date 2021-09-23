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
package org.terracotta.ehcache.tests;

import net.sf.ehcache.Cache;

import org.junit.Assert;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.collections.ToolkitMap;

public class EmbeddedEhcacheJarTestClient extends ClientBase {

  public EmbeddedEhcacheJarTestClient(String[] args) {
    super("test", args);
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    ToolkitMap<String, String> map = toolkit.getMap("testMap", null, null);
    Class ehcacheClass = map.getClass().getClassLoader().loadClass("net.sf.ehcache.Ehcache");
    // Verify that the Ehcache.class loaded from the ClusteredStateLoader is the same as that loaded from the app. Since
    // Ehcache will be on the classpath for this test, we want to verify that the app class loader version is used
    // rather than the embedded one from inside the toolkit runtime jar.
    System.out.println("Ehcache.class class loader: " + ehcacheClass.getClassLoader());
    Assert.assertTrue("Ehcache.class was not loaded from the app classloader.",
                      ehcacheClass.getClassLoader() == EmbeddedEhcacheJarTestClient.class.getClassLoader());
  }

  public static void main(String[] args) {
    new EmbeddedEhcacheJarTestClient(args).run();
  }
}
