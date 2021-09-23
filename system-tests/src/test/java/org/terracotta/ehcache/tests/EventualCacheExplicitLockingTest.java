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
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import com.tc.test.config.model.TestConfig;


public class EventualCacheExplicitLockingTest extends AbstractCacheTestBase {

  public EventualCacheExplicitLockingTest(TestConfig testConfig) {
    super("eventual-cache-explicit-locking-test.xml", testConfig, EventualCacheExplicitLockingTestClient.class,
          EventualCacheExplicitLockingTestClient.class);
  }

  public static class EventualCacheExplicitLockingTestClient extends ClientBase {

    public static final int     NUM_ELEMENTS     = 2000;
    public static final String  FIRST_VALUE_PREFIX  = "first_";
    public static final String  SECOND_VALUE_PREFIX = "second_";
    private static final String BARRIER_NAME     = "eventual-cache-explicit-locking-barrier";
    private static final int    CLIENT_COUNT     = 2;

    public EventualCacheExplicitLockingTestClient(String[] args) {
      super("eventualConsistencyCache", args);
    }

    public static void main(String[] args) {
      new EventualCacheExplicitLockingTestClient(args).run();
    }



    /**
     * Puts NUM_ELEMENTS Elements in the cache with each value prefixed by valPrefix
     */
    private void doStrongPuts(Cache cache, String valPrefix) {
      for (int i = 0; i < NUM_ELEMENTS; i++) {
        cache.acquireWriteLockOnKey(getKey(i));
        cache.put(getElement(i, valPrefix));
        cache.releaseWriteLockOnKey(getKey(i));
      }
    }

    private void doEventualGets(Cache cache) {
      for (int i = 0; i < NUM_ELEMENTS; i++) {
        cache.get(getKey(i));
      }
    }

    private boolean checkValues(Cache cache, String prefix) {
      for (int i = 0; i < NUM_ELEMENTS; i++) {
        cache.acquireReadLockOnKey(getKey(i));
        Element element = cache.get(getKey(i));
        boolean eq = getValue(i, prefix).equals(element.getObjectValue());
        cache.releaseReadLockOnKey(getKey(i));
        if (!eq) return eq;
      }
      return true;
    }

    private Element getElement(int i, String valPrefix) {
      return new Element(getKey(i), getValue(i, valPrefix));
    }

    private String getValue(int i, String valPrefix) {
      return valPrefix + "val_" + i;
    }

    private String getKey(int i) {
      return "key_" + i;
    }

    @Override
    protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {

      ToolkitBarrier barrier = toolkit.getBarrier(BARRIER_NAME, CLIENT_COUNT);

      int index = barrier.await();
      if (index == 0) {
        System.err.println("Client: " + index + " doing strong puts...");
        doStrongPuts(cache, "");
      }

      barrier.await();
      if (index == 1) {
        System.err.println("Client: " + index + " doing eventual gets");
        doEventualGets(cache);
      }

      barrier.await();
      if (index == 0) {
        System.err.println("Client: " + index + " modifying values strongly");
        doStrongPuts(cache, FIRST_VALUE_PREFIX);
      }

      barrier.await();
      if (index == 1) {
        System.err.println("Client: " + index + " checking values");
        assertTrue(checkValues(cache, FIRST_VALUE_PREFIX));
      }

      barrier.await();
      if (index == 0) {
        System.err.println("Client: " + index + " modifying values strongly again");
        doStrongPuts(cache, SECOND_VALUE_PREFIX);
      }

      barrier.await();
      if (index == 1) {
        System.err.println("Client: " + index + " checking values");
        assertTrue(checkValues(cache, SECOND_VALUE_PREFIX));
      }
    }
  }
}
