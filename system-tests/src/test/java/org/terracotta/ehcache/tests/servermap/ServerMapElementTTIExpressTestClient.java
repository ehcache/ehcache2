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

public class ServerMapElementTTIExpressTestClient extends ServerMapClientBase {

  public ServerMapElementTTIExpressTestClient(String[] args) {
    super("testWithElementEvictionTTI", args);
  }

  public static void main(String[] args) {
    new ServerMapElementTTIExpressTestClient(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
    int size = cache.getSize();
    assertEquals(0, size);
    System.out.println("Client populating cache.");

    for (int i = 0; i < 1500; i++) {
      cache.put(new Element("key-" + i, "value-" + i));
    }

    for (int i = 1500; i < 2000; i++) {
      Element element = new Element("key-" + i, "value-" + i);
      element.setTimeToIdle(60 * 4); // setting TTI of 1500-2000 keys as 4 min
      cache.put(element);
    }

    System.out.println("Cache populate. Size: " + cache.getSize());

    Date date = new Date();
    System.out.println("Sleeping for 2 mins (now=" + date + ", " + date.getTime() + ") ... ");
    // Sleep for TTI to kick in:
    Thread.sleep(2 * 60 * 1000);

    date = new Date();
    System.out.println("After sleeping 2 mins. Size: " + cache.getSize() + " (now=" + date + ", " + date.getTime()
                       + ")");

    for (int i = 0; i < 1500; i++) {
      Element element = cache.get("key-" + i);
      Assert.assertNull("Element should be null of key-" + i + " actual: " + element, element);
    }

    date = new Date();
    System.out.println("Touching elements from 1500-2000 (now=" + date + ", " + date.getTime() + ")");
    for (int i = 1500; i < 2000; i++) {
      Element element = cache.get("key-" + i);
      Assert.assertTrue(element.getValue().equals("value-" + i));
    }

    for (int i = 2000; i < 2500; i++) {
      Element element = new Element("key-" + i, "value-" + i);
      element.setTimeToIdle(15 * 60); // setting TTI of 2000-2500 keys as 15 min
      cache.put(element);
    }

    date = new Date();
    System.out.println("Sleeping for 5 mins (now=" + date + ", " + date.getTime() + ") ... ");
    // Sleep for TTI to kick in:
    Thread.sleep(5 * 60 * 1000);

    date = new Date();
    System.out.println("After sleeping 5 mins. Size: " + cache.getSize() + " (now=" + date + ", " + date.getTime()
                       + ")");

    for (int i = 1500; i < 2000; i++) {
      Element element = cache.get("key-" + i);
      Assert.assertNull("Element should be null of key-" + i + " actual: " + element, element);
    }

    for (int i = 2000; i < 2500; i++) {
      Element element = cache.get("key-" + i);
      Assert.assertTrue(element.getValue().equals("value-" + i));
    }
  }

}
