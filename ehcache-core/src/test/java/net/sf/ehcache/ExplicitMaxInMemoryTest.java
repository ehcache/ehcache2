/**
 *  Copyright Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache;

import junit.framework.Assert;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.event.CacheEventListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assume.assumeThat;

public class ExplicitMaxInMemoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplicitMaxInMemoryTest.class);

    private static final int MB = 1024 * 1024;

    @Test
    public void testExplicitMaxInMemory() throws Exception {
        assumeThat(parseInt(getProperty("java.specification.version").split("\\.")[0]), is(lessThan(16)));

        Configuration config = new Configuration();
        config.maxBytesLocalHeap(10, MemoryUnit.MEGABYTES);
        CacheManager cm = new CacheManager(config);

        try {
          CacheConfiguration cc = new CacheConfiguration("testCache", 0);
          cm.addCache(new Cache(cc));

          Cache cache = cm.getCache("testCache");
          Assert.assertEquals(0, cache.getCacheConfiguration().getMaxEntriesLocalHeap());

          CountingEvictionListener countingEvictionListener = new CountingEvictionListener();
          cache.getCacheEventNotificationService().registerListener(countingEvictionListener);

          for (int i = 0; i < 20; i++) {
              cache.put(new Element("key-" + i, new byte[MB]));
              LOGGER.info("After put: i=" + i + ", size: " + cache.getSize() + ", sizeBytes: " + cache.getStatistics().getLocalHeapSizeInBytes());
          }

          Assert.assertTrue(9 <= cache.getStatistics().getLocalHeapSize());
          Assert.assertTrue(11 >= cache.getStatistics().getLocalHeapSize());

          Assert.assertTrue(cache.getStatistics().getLocalHeapSizeInBytes() > 9 * MB);
          Assert.assertTrue(cache.getStatistics().getLocalHeapSizeInBytes() < 11 * MB);

          Assert.assertTrue(9 <= countingEvictionListener.evictionCounter.get());
          Assert.assertTrue(11 >= countingEvictionListener.evictionCounter.get());
        } finally {
          cm.shutdown();
        }

    }

    private static class CountingEvictionListener implements CacheEventListener {
        private static final AtomicInteger evictionCounter = new AtomicInteger();

        public void notifyRemoveAll(Ehcache cache) {
            //
        }

        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            //

        }

        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            //
        }

        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            //
        }

        public void notifyElementExpired(Ehcache cache, Element element) {
            //
        }

        public void notifyElementEvicted(Ehcache cache, Element element) {
            evictionCounter.incrementAndGet();
            LOGGER.info("XXXXXXX element evicted: key: " + element.getKey());
        }

        public void dispose() {
            //
        }

        @Override
        public Object clone() {
            throw new RuntimeException();
        }
    }
}
