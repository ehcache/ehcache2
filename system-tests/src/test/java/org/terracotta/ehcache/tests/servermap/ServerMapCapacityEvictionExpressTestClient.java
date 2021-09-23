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

public class ServerMapCapacityEvictionExpressTestClient extends ServerMapClientBase {

  public ServerMapCapacityEvictionExpressTestClient(String[] args) {
    super("testWithEvictionMaxElements", args);
  }

  public static void main(String[] args) {
    new ServerMapCapacityEvictionExpressTestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    int size = cache.getSize();
    assertEquals(0, size);
    System.out.println("Client populating cache.");
    for (int i = 0; i < 8000; i++) {
      cache.put(new Element(i, "value-" + i));
    }

    System.out.println("Cache populated. size: " + cache.getSize());
    System.out.println("Sleeping for 3 mins (now=" + new Date() + ") ... ");
    // Wait up to 5 min. for the capacity evictor to do its thing.
    int count = 0;
    while ( cache.getSize() > 6000 && count++ < 60) {
        Thread.sleep(5000);
        System.out.println("Cache populated. size: " + cache.getSize());
    }

    System.out.println("After sleeping for 3 mins. Size: " + cache.getSize());
    // Now size should be the capacity
    assertRange(3000, 6000, cache);

  }
}
