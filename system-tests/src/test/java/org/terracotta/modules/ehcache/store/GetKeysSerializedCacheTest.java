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
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import java.io.Serializable;
import java.util.List;

import junit.framework.Assert;

/**
 * @author Chris Dennis
 */
public class GetKeysSerializedCacheTest extends AbstractCacheTestBase {
  private static final int NODE_COUNT = 2;

  public GetKeysSerializedCacheTest(TestConfig testConfig) {
    super(testConfig, App.class, App.class);
  }

  public static class App extends ClientBase {
    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super(args);
      this.barrier = getClusteringToolkit().getBarrier("test-barrier", NODE_COUNT);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      Assert.assertEquals(0, cache.getSize());

      barrier.await();

      if (index == 0) {
        cache.put(new Element(new KeyType("key"), "value"));
      }

      barrier.await();

      String value = (String) cache.get(new KeyType("key")).getObjectValue();
      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", value);

      barrier.await();

      List keys = cache.getKeys();
      Assert.assertEquals(1, keys.size());
      Object k = keys.iterator().next();
      Assert.assertTrue(k instanceof KeyType);
      Assert.assertEquals(new KeyType("key"), k);

      barrier.await();
    }

  }

  public static class KeyType implements Serializable {

    private final String string;

    KeyType(String string) {
      this.string = string;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof KeyType && ((KeyType) o).string.equals(string);
    }

    @Override
    public int hashCode() {
      return string.hashCode();
    }
  }
}
