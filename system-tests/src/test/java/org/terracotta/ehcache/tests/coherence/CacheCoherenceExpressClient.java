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
package org.terracotta.ehcache.tests.coherence;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.TerracottaConfiguration.Consistency;

import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.test.util.WaitUtil;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import java.util.concurrent.Callable;

import junit.framework.Assert;

public class CacheCoherenceExpressClient extends ClientBase {

  public static final String PASS_OUTPUT = "CacheCoherenceExpressClient PASS output";
  private String             id;
  private ToolkitBarrier     barrier;

  public CacheCoherenceExpressClient(String[] args) {
    super("test", args);
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    barrier = toolkit.getBarrier("CacheCoherenceExpressClient", CacheCoherenceExpressTest.CLIENT_COUNT);
    int index = barrier.await();
    id = "" + index;
    log("Created barrier, index: " + index);

    // coherent="false" now means non-strict
    Assert.assertEquals(Consistency.EVENTUAL, cache.getCacheConfiguration().getTerracottaConfiguration()
        .getConsistency());

    // move to bulk load
    cache.setNodeCoherent(false);

    barrier.await();
    Assert.assertEquals(false, cache.isNodeCoherent());
    Assert.assertEquals(false, cache.isClusterCoherent());
    barrier.await();
    cache.setNodeCoherent(true);
    barrier.await();
    basicCacheTest(index, cache);
    barrier.await();
    cache.setNodeCoherent(false);
    barrier.await();

    boolean old = cache.isNodeCoherent();
    barrier.await();
    doTestDynamicConfig(index, cache);
    barrier.await();
    cache.setNodeCoherent(old);
    barrier.await();

    log("####### running cache coherence test, waiting node should get notified");
    cacheCoherenceTest(index, cache, true);

    barrier.await();
    log("####### setting cache to incoherent again in all nodes.");
    cache.setNodeCoherent(false);
    barrier.await();

    log("####### running cache coherence test, some nodes will disconnect without calling setCoherent(true)");
    // run this test last
    cacheCoherenceTest(index, cache, false);

    log(PASS_OUTPUT);
  }

  private void doTestDynamicConfig(int index, Cache cache) throws Exception {
    log("Testing dynamic config change");
    boolean old = cache.isNodeCoherent();
    if (index == 0) {
      cache.setNodeCoherent(true);
      Assert.assertEquals(true, cache.isNodeCoherent());
      // barrier 1
      barrier.await();
      // barrier 2
      barrier.await();
      cache.setNodeCoherent(false);
      Assert.assertEquals(false, cache.isNodeCoherent());
      // barrier 3
      barrier.await();
      // barrier 4
      barrier.await();
    } else {
      // barrier 1
      barrier.await();
      // validate no change in other node
      Assert.assertEquals(old, cache.isNodeCoherent());
      // barrier 2
      barrier.await();
      // barrier 3
      barrier.await();
      Assert.assertEquals(old, cache.isNodeCoherent());
      // barrier 4
      barrier.await();
    }
    log("Testing dynamic config change -- done");
  }

  private void basicCacheTest(int index, Cache cache) throws Exception {
    log("Running basicCacheTest");
    Assert.assertEquals(0, cache.getSize());

    barrier.await();

    if (index == 0) {
      cache.put(new Element("key", "value"));
    }

    barrier.await();

    Assert.assertEquals(1, cache.getSize());
    Assert.assertEquals("value", cache.get("key").getObjectValue());

    barrier.await();

    if (index == 0) {
      boolean removed = cache.remove("key");
      Assert.assertTrue(removed);
    }

    barrier.await();

    Assert.assertEquals(0, cache.getSize());
  }

  private void cacheCoherenceTest(int index, final Cache cache, boolean coherentAtEnd) throws Exception {
    barrier.await();
    Assert.assertEquals(false, cache.isNodeCoherent());
    Assert.assertEquals(false, cache.isClusterCoherent());
    if (index == 0) {
      cache.setNodeCoherent(true);
      Assert.assertEquals(true, cache.isNodeCoherent());
      Assert.assertEquals(false, cache.isClusterCoherent());
      log("Going to wait until coherent");
      barrier.await();
      cache.waitUntilClusterCoherent();
      Assert.assertEquals(true, cache.isClusterCoherent());
      log("Cache is now coherent");
      final int otherNodes = CacheCoherenceExpressTest.CLIENT_COUNT - 1;
      if (coherentAtEnd) {
        Assert.assertEquals(otherNodes, cache.getSize());
      } else {
        log("Asserting other exiting nodes committed");
        // make sure the shutdown hook was executed and 5000 elements were inserted by each node
        WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {

          @Override
          public Boolean call() throws Exception {
            System.out.println("Cache Size" + cache.getSize());
            return (5000 * otherNodes) + otherNodes == cache.getSize();
          }
        });
        log("Done");
      }
    } else {
      barrier.await();
      if (coherentAtEnd) {
        Element element = new Element("key-" + index, "value");
        cache.put(element);
        log("added element and sleeping for 10 secs: " + element);
        // 10 seconds is enough for 1 element to be flushed from the local buffer
        Thread.sleep(10 * 1000);
        log("setting cache coherent");
        cache.setNodeCoherent(true);
      } else {
        // put 5000 elements, from each node
        for (int i = 0; i < 5000; i++) {
          cache.put(new Element("node-" + index + "-key-" + i, "node-" + index + "-value-" + i));
        }
        log("Node exiting without setting back cache in coherent.");
      }
    }
  }

  private void log(String string) {
    System.out.println("Node-" + id + ": " + string);
  }
}