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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;

import com.tc.test.config.model.TestConfig;

import junit.framework.Assert;

public class IncoherentNodesTest extends AbstractCacheTestBase {

  public IncoherentNodesTest(TestConfig testConfig) {
    super("cache-coherence-test.xml", testConfig, IncoherentNodesTestClientOne.class,
          IncoherentNodesTestClientTwo.class);
  }

  public static class IncoherentNodesTestClientOne extends ClientBase {

    public IncoherentNodesTestClientOne(String[] args) {
      super("test", args);
    }

    public static void main(String[] args) {
      new IncoherentNodesTestClientOne(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      doTest(cache, toolkit, true);
    }

    public void doTest(Cache cache, Toolkit toolkit, final boolean killInBetween) {
      log("Running test. killInBetween: " + killInBetween);
      cache.setNodeCoherent(false);
      log(" node set to incoherent");

      if (killInBetween) {
        Thread th = new Thread(new Runnable() {

          public void run() {
            log("started the killer thread....");
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              throw new AssertionError(e);
            }
            log("Exiting vm");
            IncoherentNodesTestClientOne.this.pass();
            Runtime.getRuntime().halt(0);
          }
        });
        th.start();
      }

      try {
        // load data
        int i = 0;
        final int numCycles = 10;
        while (true) {
          log("loading fake data now....");
          cache.put(new Element("key-" + i, "value-" + i));
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            /**/
          }
          if (i++ == numCycles) {
            log(" committing now....");
            break;
          }
        }
      } catch (Exception e) {
        throw new AssertionError(e);
      }

      if (killInBetween) { throw new AssertionError("this node should had been killed by now...."); }

      cache.setNodeCoherent(true);
      cache.waitUntilClusterCoherent();
      Assert.assertTrue(cache.isClusterCoherent());
      log("done....");
    }

    private static void log(String string) {
      System.out.println("__XXX__ " + string);
    }

  }

  public static class IncoherentNodesTestClientTwo extends IncoherentNodesTestClientOne {

    public IncoherentNodesTestClientTwo(String[] args) {
      super(args);
    }

    public static void main(String[] args) {
      new IncoherentNodesTestClientTwo(args).run();
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      doTest(cache, toolkit, false);
    }

  }
}
