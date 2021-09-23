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
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.test.util.WaitUtil;

import com.tc.properties.TCPropertiesConsts;
import com.tc.test.config.model.TestConfig;

import java.util.concurrent.Callable;

import junit.framework.Assert;

public class TotalCapacityTest extends AbstractCacheTestBase {

  public TotalCapacityTest(TestConfig testConfig) {
    super("total-capacity-cache-test.xml", testConfig, App.class, App.class);
    testConfig.getClientConfig().setParallelClients(true);
    testConfig.addTcProperty(TCPropertiesConsts.EHCACHE_EVICTOR_LOGGING_ENABLED, "true");
    testConfig.addTcProperty(TCPropertiesConsts.L2_SERVERMAP_EVICTION_CLIENTOBJECT_REFERENCES_REFRESH_INTERVAL, "1000");
    testConfig.addTcProperty(TCPropertiesConsts.EHCACHE_STORAGESTRATEGY_DCV2_EVICTION_OVERSHOOT, "0.001");
    testConfig.addTcProperty(TCPropertiesConsts.EHCACHE_STORAGESTRATEGY_DCV2_PERIODICEVICTION_ENABLED, "true");
  }

  public static class App extends ClientBase {

    public App(String[] args) {
      super(args);
    }

    public static void main(String[] args) {
      new App(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = getBarrierForAllClients().await();

      final Cache cache1 = cacheManager.getCache("test1");

      if (index == 0) {
        // Fill cache up to the max capacity = 1000 elements
        // Fill cache up to the max capacity = 10 elements
        for (int i = 1; i <= 5000; i++) {
          cache1.put(new Element("key" + i, "value" + i));
          Assert.assertEquals(i, cache1.getSize());
        }
      }

      getBarrierForAllClients().await();

      // In another node, add additional elements which should trigger an eviction
      if (index == 1) {
        for (int i = 5000; i <= 5751; i++) {
          Thread.sleep(10);
          System.out.println("XXX putting " + i + " size " + cache1.getSize());
        }
        cache1.getSize();
      }
      getBarrierForAllClients().await();
      WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
        public Boolean call() throws Exception {
          return 5000 == cache1.getSize();
        }
      });
    }

  }

}
