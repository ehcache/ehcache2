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

import java.util.Set;

import junit.framework.Assert;

public class ClusteredEventsAllTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 5;

  public ClusteredEventsAllTest(TestConfig testConfig) {
    super("clustered-events-test.xml", testConfig, App.class, App.class, App.class, App.class, App.class);
    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.ehcache.clusteredStore.checkContainsKeyOnPut=true");
  }

  public static class App extends ClientBase {
    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super("testAll", args);
      this.barrier = getClusteringToolkit().getBarrier("test-barrier", NODE_COUNT);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      Assert.assertEquals(0, cache.getSize());

      barrier.await();

      cache.put(new Element("key" + index, "value" + index));
      cache.put(new Element("key" + index, "valueUpdated" + index));
      cache.remove("key" + index);

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
    }
  }

}
