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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.event.CacheEventListenerAdapter;
import net.sf.ehcache.event.TerracottaCacheEventReplicationFactory;

import org.junit.Assert;
import org.terracotta.test.util.WaitUtil;
import org.terracotta.toolkit.Toolkit;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Alex Snaps
 */
public class ClusterCacheEventsRejoinEnabledClient extends ClientBase {

  private static final int    ELEMENTS = 10;
  private final AtomicInteger counter  = new AtomicInteger();

  public ClusterCacheEventsRejoinEnabledClient(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new ClusterCacheEventsRejoinEnabledClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    final int nodeId = getBarrierForAllClients().await();

    final CacheCluster cluster = cache.getCacheManager().getCluster(ClusterScheme.TERRACOTTA);
    Assert.assertTrue(cluster != null);
    Assert.assertTrue(cluster.getScheme().equals(ClusterScheme.TERRACOTTA));
    System.out.println("WELCOME TO " + cluster.getCurrentNode().getId());

    try {
      cache.getCacheEventNotificationService().registerListener(new TerracottaCacheEventReplicationFactory()
                                                                    .createCacheEventListener(null));
      cache.getCacheEventNotificationService().registerListener(new CacheEventListenerAdapter() {
        @Override
        public void notifyElementPut(final Ehcache ehcache, final Element element) throws CacheException {
          System.out.println(cluster.getCurrentNode().getId() + " GOT A VALUE FOR " + element.getKey());
          counter.getAndIncrement();
        }
      });
      getBarrierForAllClients().await();
      if (nodeId == 0) {
        for (int i = 0; i < ELEMENTS; i++) {
          final Element element = new Element(Integer.toString(i) + " FROM " + cluster.getCurrentNode().getId(),
                                              "Value for " + Integer.toString(i));
          cache.put(element);
          System.out.println(cluster.getCurrentNode().getId() + " PUTS A VALUE FOR " + element.getKey());
        }
      }
      getBarrierForAllClients().await();
      WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          int counterValue = counter.get();
          System.out.println("Waiting until counter hits expected: " + ELEMENTS + ", Actual: " + counterValue);
          if (counterValue == ELEMENTS) { return true; }
          return false;
        }
      });
      Assert.assertEquals(counter.get(), ELEMENTS);
    } finally {
      getBarrierForAllClients().await();
    }
  }
}
