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
package org.terracotta.ehcache.tests.servermap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;

import java.util.Date;
import java.util.concurrent.BrokenBarrierException;

public class ServerMapL2EvictionReachesOneL1TestClient extends ServerMapClientBase {
  final static long EXPECTED_EVICTION_COUNT = 3000;

  public ServerMapL2EvictionReachesOneL1TestClient(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new ServerMapL2EvictionReachesOneL1TestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    System.out.println("Running test with concurrency=1");
    testWith(cache, 3000, EXPECTED_EVICTION_COUNT, clusteringToolkit);

    System.out.println("Testing with higher concurrency value.");
    // 100 maxElementsOnDisk, 50 stripes -> targetMaxTotalCount of 2 for each stripe
    // add 5000 (100 per stripe), at least one per stripe should be evicted
  }

  private void testWith(final Cache cache, final int maxElements, final long expectedEvictionCount,
                        Toolkit clusteringToolkit) throws InterruptedException, BrokenBarrierException {
    EvictionCountingEventListener countingListener = new EvictionCountingEventListener(
                                                                                       clusteringToolkit
                                                                                           .getAtomicLong("EvictionCounter"));
    cache.getCacheEventNotificationService().registerListener(countingListener);

    for (int i = 0; i < maxElements; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }
    long value = countingListener.getEvictedCount();
    System.out.println("Sleeping for 2 mins (now = " + new Date() + "), evicted till now: " + value);
    Thread.sleep(2 * 60 * 1000);

    getBarrierForAllClients().await();

    value = countingListener.getEvictedCount();
    System.out.println("After sleeping 2 mins: value=" + value);
    assertTrue("Expected at most " + expectedEvictionCount + " elements to have been evicted, value=" + value,
               (value <= expectedEvictionCount));
  }
}