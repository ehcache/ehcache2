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
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import com.tc.test.config.model.TestConfig;

import java.io.Serializable;
import java.util.Set;

import junit.framework.Assert;

public class ClusteredEventsSerializationTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 5;

  public ClusteredEventsSerializationTest(TestConfig testConfig) {
    super("clustered-events-test.xml", testConfig, App.class, App.class, App.class, App.class, App.class);
    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.ehcache.clusteredStore.checkContainsKeyOnPut=true");
  }

  public static class App extends ClientBase {
    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super("testSerialization", args);
      this.barrier = getClusteringToolkit().getBarrier("test-barrier", NODE_COUNT);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      Assert.assertEquals(0, cache.getSize());

      barrier.await();

      NonPortable key = new NonPortable("key" + index);
      NonPortable valuePut = new NonPortable("value" + index);
      NonPortable valueUpdate = new NonPortable("valueUpdated" + index);
      cache.put(new Element(key, valuePut));
      cache.put(new Element(key, valueUpdate));
      cache.remove(key);

      barrier.await();

      cache.removeAll();

      barrier.await();

      Thread.sleep(10000);

      EhcacheTerracottaEventListener listener = null;
      Set<CacheEventListener> listeners = cache.getCacheEventNotificationService().getCacheEventListeners();
      for (CacheEventListener l : listeners) {
        if (l instanceof EhcacheTerracottaEventListener) {
          listener = (EhcacheTerracottaEventListener) l;
          break;
        }
      }

      Assert.assertNotNull(listener);

      Assert.assertEquals(NODE_COUNT, listener.getPut().size());
      Assert.assertEquals(NODE_COUNT, listener.getUpdate().size());
      Assert.assertEquals(NODE_COUNT, listener.getRemove().size());
      Assert.assertEquals(NODE_COUNT, listener.getRemoveAll());

      boolean foundPutKey = false;
      for (Element element : listener.getPut()) {
        if (element.getObjectKey().equals(key)) {
          foundPutKey = true;
          Assert.assertEquals(valuePut, element.getObjectValue());
        } else {
          Assert.assertEquals("value" + element.getObjectKey().toString().substring("key".length()), element
              .getObjectValue().toString());
        }
      }
      Assert.assertTrue(foundPutKey);

      boolean foundUpdateKey = false;
      for (Element element : listener.getUpdate()) {
        if (element.getObjectKey().equals(key)) {
          foundUpdateKey = true;
          Assert.assertEquals(valueUpdate, element.getObjectValue());
        } else {
          Assert.assertEquals("valueUpdated" + element.getObjectKey().toString().substring("key".length()), element
              .getObjectValue().toString());
        }
      }
      Assert.assertTrue(foundUpdateKey);

      boolean foundRemoveKey = false;
      for (Element element : listener.getRemove()) {
        if (element.getObjectKey().equals(key)) {
          foundRemoveKey = true;
        }
      }
      Assert.assertTrue(foundRemoveKey);
    }

  }

  public static class NonPortable implements Serializable {
    private final String value;

    public NonPortable(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NonPortable that = (NonPortable) o;

      if (value != null ? !value.equals(that.value) : that.value != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return value != null ? value.hashCode() : 0;
    }
  }
}
