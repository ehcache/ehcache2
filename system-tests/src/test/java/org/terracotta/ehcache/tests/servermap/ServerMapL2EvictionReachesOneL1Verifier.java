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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.ehcache.tests.ClientBase;

import junit.framework.Assert;

public class ServerMapL2EvictionReachesOneL1Verifier extends ClientBase {

  public ServerMapL2EvictionReachesOneL1Verifier(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new ServerMapL2EvictionReachesOneL1Verifier(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    System.out.println("in the verifier");

    EvictionCountingEventListener countingListener = new EvictionCountingEventListener(
                                                                                       clusteringToolkit
                                                                                           .getAtomicLong("EvictionCounter"));
    cache.getCacheEventNotificationService().registerListener(countingListener);

    getBarrierForAllClients().await();
    long value = countingListener.getEvictedCount();
    System.out.println("After sleeping 2 mins: value=" + value);
    Assert.assertTrue("Expected at most " + ServerMapL2EvictionReachesOneL1TestClient.EXPECTED_EVICTION_COUNT
                          + " elements to have been evicted, value=" + value,
                      (value <= ServerMapL2EvictionReachesOneL1TestClient.EXPECTED_EVICTION_COUNT));
  }
}
