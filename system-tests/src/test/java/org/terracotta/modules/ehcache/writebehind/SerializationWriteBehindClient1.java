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

import org.terracotta.ehcache.tests.AbstractWriteBehindClient;
import org.terracotta.ehcache.tests.WriteBehindCacheWriter;
import org.terracotta.toolkit.Toolkit;

public class SerializationWriteBehindClient1 extends AbstractWriteBehindClient {
  public SerializationWriteBehindClient1(String[] args) {
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
    new SerializationWriteBehindClient1(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    cache.registerCacheWriter(new WriteBehindCacheWriter(this));
    for (int i = 0; i < 1000; i++) {
      cache.putWithWriter(new Element(new SerializationWriteBehindType("key" + i % 200), new SerializationWriteBehindType("value" + i)));
      if (0 == i % 10) {
        cache.removeWithWriter(new SerializationWriteBehindType("key" + i % 200 / 10));
      }
    }

    while (getWriteCount() <= 100) {
      Thread.sleep(200);
    }
  }
}