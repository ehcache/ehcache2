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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

public class PrimitiveClassTest extends AbstractCacheTestBase {
  private static final int NODE_COUNT = 3;

  public PrimitiveClassTest(TestConfig testConfig) {
    super("primitive-class-test.xml", testConfig, App.class, App.class, App.class);
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

      Set<Class<?>> types = new HashSet<Class<?>>();
      types.add(Void.TYPE);
      types.add(Boolean.TYPE);
      types.add(Byte.TYPE);
      types.add(Character.TYPE);
      types.add(Double.TYPE);
      types.add(Float.TYPE);
      types.add(Integer.TYPE);
      types.add(Long.TYPE);
      types.add(Short.TYPE);

      if (index == 0) {
        for (Class<?> c : types) {
          cache.put(new Element(c, c));
        }
      }

      barrier.await();

      for (Class<?> c : types) {
        Assert.assertEquals(c, cache.get(c).getObjectValue());
        Assert.assertEquals(c, cache.get(c).getObjectKey());
      }

      Set<Class<?>> copy = new HashSet<Class<?>>(types);
      for (Object o : cache.getKeys()) {
        boolean removed = copy.remove(o);
        if (!removed) { throw new AssertionError("did not remove: " + o); }
      }

      Assert.assertEquals(copy.toString(), 0, copy.size());

    }

  }

}
