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

public class LockGCNotFlushingEntriesTestClient extends ServerMapClientBase {

  public LockGCNotFlushingEntriesTestClient(String[] args) {
    super("testLockGC", args);
  }

  public static void main(String[] args) {
    new LockGCNotFlushingEntriesTestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    int size = cache.getSize();
    assertEquals(0, size);
    System.out.println("Client populating cache.");
    for (int i = 0; i < 1000; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }

    System.out.println("Cache populated. Sleeping for 120 secs. size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());
    Thread.sleep(120 * 1000);

    System.out.println("After sleeping 120 secs. size: " + cache.getSize() + " inMemorySize: "
                       + cache.getStatistics().getLocalHeapSize());
    // assert range as some may have got evicted while populating cache
    assertTrue(1000 == cache.getSize());
    assertTrue(1000 == cache.getStatistics().getLocalHeapSize());
  }
}
