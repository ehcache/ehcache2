/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package org.terracotta.ehcache.tests;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

public class ExplicitTryLockTest extends AbstractCacheTestBase {

  public ExplicitTryLockTest(TestConfig testConfig) {
    super("eventual-cache-explicit-locking-test.xml", testConfig, TryLockClient.class, TryLockClient.class);
  }
  
  public static class TryLockClient extends ClientBase {

    public TryLockClient(String[] args) {
      super("strongConsistencyCache", args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit myToolkit) throws Throwable {
      
      int index = waitForAllClients();
      
      assertTrue(cache.isTerracottaClustered());
      assertTrue(cache.getName().equals("strongConsistencyCache"));
      
      if(index == 0) {
        cache.put(new Element(1, 1));
        cache.acquireWriteLockOnKey(1);
      }
      
      index = waitForAllClients();
      
      if(index == 1) {
        cache.put(new Element(2, 2));
        getTestControlMbean().crashActiveServer(0);
        assertFalse(cache.tryWriteLockOnKey(1, 20));
      }
      
      waitForAllClients();
      
    }
    
  }

}
