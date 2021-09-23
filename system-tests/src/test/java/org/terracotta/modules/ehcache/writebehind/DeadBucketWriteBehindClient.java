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
import org.terracotta.toolkit.concurrent.ToolkitBarrier;

import java.util.concurrent.TimeUnit;

public class DeadBucketWriteBehindClient extends AbstractWriteBehindClient {

  public DeadBucketWriteBehindClient(String[] args) {
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
    new WriteBehindClient1(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    ToolkitBarrier barrier = toolkit.getBarrier(DistributionDeadBucketWriteBehindTest.DISTRIBUTION_BARRIER_NAME, DistributionDeadBucketWriteBehindTest.NODE_COUNT);
    cache.registerCacheWriter(new WriteBehindCacheWriter(this));
    for (int i = 0; i < 500; i++) {
      cache.putWithWriter(new Element("key" + i % 200, "value" + i)); // 500 write operation
      if (0 == i % 10) {
        cache.removeWithWriter("key" + i % 200 / 10); // 50 delete operation
      }
    }

    WriteBehindManager wbManager = ((WriteBehindManager) cache.getWriterManager());
    int index = barrier.await();
    long size = wbManager.getQueueSize();
    System.out.println("client " + index + " write behind queue size " + size);
    if (index == 0 || index == 1) {
      while (size > 0) {
        System.out.println("write behind queue size " + size);
        TimeUnit.SECONDS.sleep(1L);
        size = wbManager.getQueueSize();
      }
      TimeUnit.MINUTES.sleep(1L);
    }
  }
}
