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
package org.terracotta.modules.ehcache.coherence;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

/**
 * NewCASEventualCacheTest
 */
public class NewCASEventualCacheTest extends AbstractCacheTestBase {

    public NewCASEventualCacheTest(TestConfig testConfig) {
        super("cache-coherence-test.xml", testConfig, NewCASEventualCacheTestClient.class, NewCASEventualCacheTestClient.class);
    }


    public static class NewCASEventualCacheTestClient extends ClientBase {

        public NewCASEventualCacheTestClient(String[] args) {
            super(args);
        }

        @Override
        protected void runTest(Cache cache, Toolkit myToolkit) throws Throwable {
            int i = waitForAllClients();

            Element elem = new Element("key", 0);
            boolean wonPutIfAbsent = cache.putIfAbsent(elem) == null;
            if (wonPutIfAbsent) {
                System.out.println("Won putIfAbsent");
            } else {
                System.out.println("Lost putIfAbsent");
            }


            int count = 0;
            while (count < 50) {
                Element currentElem = cache.get("key");
                Element newElem = new Element("key", ((Integer)currentElem.getObjectValue() + 1));
                while (!cache.replace(currentElem, newElem)) {
                    System.out.println("Lost replace race - getting value");
                    currentElem = cache.get("key");
                    newElem = new Element("key", ((Integer)currentElem.getObjectValue() + 1));
                }
                count++;
            }

            waitForAllClients();

            Element endElem;
            cache.acquireReadLockOnKey("key");
            try {
                endElem = cache.get("key");
                assertEquals(endElem.getObjectValue(), 100);
            } finally {
                cache.releaseReadLockOnKey("key");
            }

            waitForAllClients();

            boolean wonRemove = cache.removeElement(endElem);
            if (wonRemove) {
                System.out.println("Won removeElement");
            } else {
                System.out.println("Lost removeElement");
            }

        }

        @Override
        protected Cache getCache() {
          return cacheManager.getCache("non-strict-Cache");
        }
    }
}
