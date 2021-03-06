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

public class ServerMapL1CapacityEvictionExpressTestClient extends ServerMapClientBase {

  public ServerMapL1CapacityEvictionExpressTestClient(String[] args) {
    super("testWithMaxElementsInMemory", args);
  }

  public static void main(String[] args) {
    new ServerMapL1CapacityEvictionExpressTestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    assertLocalCache(true);
    int size = cache.getSize();
    assertEquals(0, size);
    System.out.println("Client populating cache.");
    for (int i = 0; i < 5100; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }

    System.out.println("Cache populated. Sleeping for 3 secs. size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());
    Thread.sleep(3000);

    System.out.println("After sleeping 3 secs. size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());
    // assert range as some may have got evicted while populating cache
    assertRange(4500, 5100, cache.getSize());
    assertRange(0, 5100, cache.getStatistics().getLocalHeapSize());

    // add some more elements, to get eviction kicking if not already
    for (int i = 5100; i < 5200; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }

    System.out.println("After adding 100 more. size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());

    System.out.println("Sleeping for 1.5 mins (now=" + new Date() + ") ... ");
    // Wait a bit for the capacity evictor to do its thing.
    Thread.sleep(15 * 6 * 1000);

    System.out.println("After sleeping for 1.5 mins. Size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());
    // l1 cacacity evicts 20% extra of maxInMemory
    assertRange(0, 5000, cache.getStatistics().getLocalHeapSize());

  }

  private void assertRange(int min, int max, long actual) {
    assertTrue("assert range failed: min: " + min + " max: " + max + " actual: " + actual, min <= actual
                                                                                           && actual <= max);
  }

  private void assertLocalCache(boolean enabled) throws Exception {
    String expected = enabled ? "true" : "false";
    String property = System.getProperty("com.tc.ehcache.storageStrategy.dcv2.localcache.enabled");
    if (property == null || !expected.equalsIgnoreCase(property)) { throw new Exception(
                                                                                        "This client needs to be run with local cache enabled"); }
  }

}
