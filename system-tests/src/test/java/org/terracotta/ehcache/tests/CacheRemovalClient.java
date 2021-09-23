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
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.cluster.ClusterInfo;

import java.lang.reflect.Field;
import java.util.List;

import junit.framework.Assert;

/**
 * @author Alex Snaps
 */
public class CacheRemovalClient extends ClientBase {

  public CacheRemovalClient(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new Client1(args).run();
  }

  @Override
  protected void runTest(final Cache cache, final Toolkit toolkit) throws Throwable {
    ClusterInfo clusterInfo = toolkit.getClusterInfo();
    List listeners = getValueOfDeclaredField("listeners", getValueOfDeclaredField("dsoCluster", clusterInfo));
    final int initialListenersSize = listeners.size();
    for (int i = 0; i < 10; i++) {
      addAndRemove("test-" + i);
    }

    // CacheManager has seen 11 caches (including the 'test' one), ...
    listeners = getValueOfDeclaredField("listeners", getValueOfDeclaredField("dsoCluster", clusterInfo));
    final int size = listeners.size();
    System.err.println("Found " + size + " listeners :");
    for (Object listener : listeners) {
      System.err.println(" - " + listener + " wrapping " + getValueOfDeclaredField("listener", listener));
    }

    // .. but there shouldn't be any listeners associated with these, but the "main" listener from the CacheManager
    cacheManager.shutdown();
    Assert.assertEquals(size, initialListenersSize);
  }

  private void addAndRemove(final String name) {
    cacheManager.addCache(new Cache(new CacheConfiguration(name, 100).terracotta(new TerracottaConfiguration()
        .clustered(true))));
    cacheManager.removeCache(name);
  }

  private static <T> T getValueOfDeclaredField(final String fieldName, final Object target) {
    try {
      final Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return (T) field.get(target);
    } catch(NoSuchFieldException e) {
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
