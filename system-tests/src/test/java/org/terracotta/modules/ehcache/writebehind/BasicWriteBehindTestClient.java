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
package org.terracotta.modules.ehcache.writebehind;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.writebehind.WriteBehindManager;

import org.terracotta.ehcache.tests.AbstractWriteBehindClient;
import org.terracotta.ehcache.tests.WriteBehindCacheWriter;
import org.terracotta.toolkit.Toolkit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

public class BasicWriteBehindTestClient extends AbstractWriteBehindClient {
  private static final int ELEMENT_COUNT = BasicWriteBehindTest.ELEMENT_COUNT;

  public BasicWriteBehindTestClient(String[] args) {
    super(args);
  }

  @Override
  public long getSleepBetweenWrites() {
    return 100L;
  }

  @Override
  public long getSleepBetweenDeletes() {
    return 100L;
  }

  public static void main(String[] args) {
    new BasicWriteBehindTestClient(args).run();
  }

  @Override
  protected void runTest(final Cache cache, Toolkit toolkit) throws Throwable {
    cache.registerCacheWriter(new WriteBehindCacheWriter(this));
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      cache.putWithWriter(new Element("key" + i % 200, "value" + i)); // 200 different keys, write operation
      if (0 == i % 10) {
        cache.removeWithWriter("key" + i % 200 / 10); // 10 different keys, delete operation
      }
    }

    final WriteBehindManager wbManager = ((WriteBehindManager) cache.getWriterManager());

    System.out.println("write behind queue size " + wbManager.getQueueSize());
    System.out.println("write behind queue size (stats)" + cache.getStatistics().getWriterQueueLength());
    // can't really do this as it would be racy: Assert.assertEquals(wbManager.getQueueSize(), cache.getStatistics().getWriterQueueLength());
    // let's take a moment and assure we foundthe statistic and stitched it all together.
    Assert.assertFalse(cache.getStatistics().getExtended().writerQueueLength().getClass().getName().contains("NullStatistic"));
    final AtomicLong counter = new AtomicLong();
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        long count = counter.incrementAndGet();
        cache.putWithWriter(new Element("key-" + count, "value-" + count));
        System.out.println("executor write behind queue size " + wbManager.getQueueSize() + " counter " + count);
      }
    }, 500L, 1L, TimeUnit.MILLISECONDS);

    // done with put now shutdown cache manager
    // this call should wait write behind queue to get empty
    Thread.sleep(TimeUnit.SECONDS.toMillis(1L));
    System.out.println("calling cacheManager shutdown");
    cache.getCacheManager().shutdown();
    
    try {
      wbManager.getQueueSize();
      Assert.fail("should have failed because cacheManager.shutdown is called before");
    } catch (IllegalStateException e) {
      // expected exception
    }
  }
}
