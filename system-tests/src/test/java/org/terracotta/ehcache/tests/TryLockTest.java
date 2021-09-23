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

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

import com.tc.test.config.model.TestConfig;

// test for ENG-680, should be enabled when implemented
public class TryLockTest extends AbstractCacheTestBase {

  public TryLockTest(TestConfig testConfig) {
    super(testConfig, TryLockTestClient.class, TryLockTestClient.class);
    disableTest();
  }

  public static class TryLockTestClient extends ClientBase {

    public TryLockTestClient(String[] args) {
      super(args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit myToolkit) throws Throwable {
      ToolkitReadWriteLock myLock = myToolkit.getReadWriteLock("abc");
      int index = waitForAllClients();

      if (index == 0) {
        myLock.writeLock().lock();
        myLock.writeLock().unlock();
      }

      waitForAllClients();

      if (index != 0) {
        assertTrue(myLock.readLock().tryLock());
        // assertTrue(myLock.readLock().tryLock(2, TimeUnit.SECONDS)); - this works
      }

      waitForAllClients();
    }
  }
}
