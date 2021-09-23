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

import junit.framework.Assert;

public class ServerMapTTIExpressTestClient extends ServerMapClientBase {

  public ServerMapTTIExpressTestClient(String[] args) {
    super("testWithEvictionTTI", args);
  }

  public static void main(String[] args) {
    new ServerMapTTIExpressTestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    int size = cache.getSize();
    assertEquals(0, size);
    System.out.println("Client populating cache.");
    long current = System.currentTimeMillis();
    for (int i = 0; i < 7000; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }
    System.out.println("Cache populate. Size: " + cache.getSize());
    // assert range as some may already have got evicted while populating
    if ( cache.getCacheConfiguration().getTimeToIdleSeconds() * 1000 < System.currentTimeMillis() - current ) {
      System.out.append("time to put " + (System.currentTimeMillis() - current));
      System.out.append("test environment is too slow. aborting. time to put:" + (System.currentTimeMillis() - current));
      return;
    } else {
      assertRange(5000, 7000, cache);
    }

    System.out.println("Sleeping for 3 mins (now=" + new Date() + ") ... ");
    Thread.sleep(1 * 60 * 1000);
    // Sleep for TTI to kick in:
    // Wait up to 30 sec. for the capacity evictor to do its thing.
    int count = 0;
    while ( cache.getSize() > 0 && count++ < 60) {
        Thread.sleep(5000);
        System.out.println("Cache populated. size: " + cache.getSize());
    }
    
    System.out.println("After sleeping for up to 5 mins. Size: " + cache.getSize());
    
    assertEquals(0, cache.getSize());
  }

}
