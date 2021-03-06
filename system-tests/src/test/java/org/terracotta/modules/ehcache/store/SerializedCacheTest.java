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

import com.tc.l2.L2DebugLogging.LogLevel;
import com.tc.object.servermap.localcache.impl.ServerMapLocalCacheImpl;
import com.tc.test.config.model.TestConfig;

import java.io.Serializable;

import junit.framework.Assert;

public class SerializedCacheTest extends AbstractCacheTestBase {
  private static final int NODE_COUNT = 3;

  public SerializedCacheTest(TestConfig testConfig) {
    super(testConfig, App.class, App.class, App.class);
    configureTCLogging(ServerMapLocalCacheImpl.class.getName(), LogLevel.DEBUG);
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
        ValueHolder value = new ValueHolder("value");
        cache.put(new Element("key", value));
      }

      waitForAllCurrentTransactionsToComplete(cache);
      barrier.await();

      ValueHolder value = (ValueHolder) cache.get("key").getObjectValue();
      Assert.assertEquals(1, cache.getSize());
      Assert.assertEquals("value", value.getData());

      // test that copyOnRead defaults to false
      ValueHolder value2 = (ValueHolder) cache.get("key").getObjectValue();
      Assert.assertTrue("$&$&$&$&$&$&$   value = " + value + " value2 = " + value2
                        + " and they are not equal $&$&$&$&$&$&$", value == value2);

      barrier.await();

      if (index == 0) {
        value.setData("updatedvalue");
      }

      barrier.await();

      if (index != 0) {
        Assert.assertFalse("updatedvalue".equals(value.getData()));
      }

      if (index == 0) {
        boolean removed = cache.remove("key");
        Assert.assertTrue(removed);
      }

      barrier.await();
      waitForAllCurrentTransactionsToComplete(cache);
      Assert.assertEquals(0, cache.getSize());
    }

  }

  public static class ValueHolder implements Serializable {
    private String data;

    public ValueHolder(final String data) {
      setData(data);
    }

    public synchronized String getData() {
      return data;
    }

    public synchronized void setData(final String data) {
      this.data = data;
    }
  }

}
