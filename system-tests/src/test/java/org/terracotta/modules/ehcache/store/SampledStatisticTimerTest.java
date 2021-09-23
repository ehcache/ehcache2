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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import java.util.Map;

import junit.framework.Assert;

public class SampledStatisticTimerTest extends AbstractCacheTestBase {

  public SampledStatisticTimerTest(TestConfig testConfig) {
    super("sampled-statistic-timer-test.xml", testConfig, App.class);
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
      for (int i = 0; i < 10; i++) {
        String cacheName = "cache-" + i;
        cacheManager.addCache(cacheName);
      }

      Map<Thread, StackTraceElement[]> liveThreads = Thread.getAllStackTraces();
      int samplerThreadCount = 0;
      for (Thread t : liveThreads.keySet()) {
        String threadName = t.getName();
        if (threadName.contains("SampledStatisticsManager Timer")) {
          samplerThreadCount++;
        }
      }
      Assert.assertEquals("Found statistic sampler threads!.", 0, samplerThreadCount);
    }
  }
}
