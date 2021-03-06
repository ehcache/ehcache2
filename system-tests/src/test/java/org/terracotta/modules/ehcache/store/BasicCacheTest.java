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

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import com.tc.test.config.model.TestConfig;

import java.util.Collections;
import java.util.Iterator;

import junit.framework.Assert;

public class BasicCacheTest extends AbstractCacheTestBase {
  private static final int NODE_COUNT = 3;

  public BasicCacheTest(TestConfig testConfig) {
    super(testConfig, App.class, App.class, App.class);
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

      // XXX: assert that the cache is clustered via methods on cache config (when methods exist)

      Assert.assertEquals(0, cache.getSize());

      barrier.await();

      if (index == 0) {
        cache.put(new Element("key", "value"));
      }

      barrier.await();

      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", cache.get("key").getObjectValue());
      Assert.assertEquals(1, cache.getKeys().size());
      Assert.assertEquals("key", cache.getKeys().iterator().next());

      barrier.await();

      testKeysetMutations(cache);

      barrier.await();

      // make sure the cache is still valid after the key set mutations above
      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", cache.get("key").getObjectValue());
      Assert.assertEquals(1, cache.getKeys().size());
      Assert.assertEquals("key", cache.getKeys().iterator().next());

      barrier.await();

      if (index == 0) {
        boolean removed = cache.remove("key");
        Assert.assertTrue(removed);
      }

      barrier.await();

      Assert.assertEquals(0, cache.getSize());
    }

    private void testKeysetMutations(Cache cache) {
      try {
        cache.getKeys().clear();
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().add("sdf");
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().add(0, "sdf");
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().addAll(Collections.singletonList("sdfsfd"));
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().addAll(0, Collections.singletonList("SDfsdf"));
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      {
        Iterator iter = cache.getKeys().iterator();
        iter.next();
        try {
          iter.remove();
          Assert.fail();
        } catch (UnsupportedOperationException uoe) {
          // expected
        }
      }

      try {
        cache.getKeys().listIterator();
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected for now, but if listIterator() gets implemented this test should make sure you can't mutate the
        // cache through it)
      }

      try {
        cache.getKeys().listIterator(0);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected for now, but if listIterator() gets implemented this test should make sure you can't mutate the
        // cache through it)
      }

      try {
        cache.getKeys().remove(0);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      Assert.assertTrue(cache.getKeys().contains("key"));
      try {
        cache.getKeys().remove("key");
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().removeAll(Collections.singletonList("key"));
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      Assert.assertFalse(cache.getKeys().contains("not in the cache!"));
      try {
        cache.getKeys().retainAll(Collections.singletonList("not in the cache!"));
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().set(0, this);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected
      }

      try {
        cache.getKeys().subList(0, 0);
        Assert.fail();
      } catch (UnsupportedOperationException uoe) {
        // expected for now, but if subList() gets implemented this test should make sure you can't mutate the
        // cache through it (or its further iterators!)
      }
    }

  }

}
