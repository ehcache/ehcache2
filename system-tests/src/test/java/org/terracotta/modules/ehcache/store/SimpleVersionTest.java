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

import java.util.concurrent.BrokenBarrierException;

import junit.framework.Assert;

public class SimpleVersionTest extends AbstractCacheTestBase {

  private static final int NODE_COUNT = 2;

  public SimpleVersionTest(TestConfig testConfig) {
    super("simple-version-test.xml", testConfig, App.class, App.class);
  }

  public static class App extends ClientBase {

    private final ToolkitBarrier barrier;

    public App(String[] args) {
      super(args);
      this.barrier = getClusteringToolkit().getBarrier("test", NODE_COUNT);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      final int index = barrier.await();

      testVersion(index, cacheManager.getCache("serialized"));
    }

    private void testVersion(final int index, Cache cache) throws InterruptedException, BrokenBarrierException {
      if (index == 0) {
        Element e1 = new Element("key", "value");
        e1.setVersion(12345);

        cache.put(e1);
      }

      barrier.await();

      Assert.assertEquals(12345, cache.get("key").getVersion());
    }

  }
}
