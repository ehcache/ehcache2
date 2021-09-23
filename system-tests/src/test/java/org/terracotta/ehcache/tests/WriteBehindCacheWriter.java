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

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.AbstractCacheWriter;

import java.util.Collection;

public class WriteBehindCacheWriter extends AbstractCacheWriter {
  private final AbstractWriteBehindClient writeBehindClient;

  public WriteBehindCacheWriter(AbstractWriteBehindClient writeBehindClient) {
    this.writeBehindClient = writeBehindClient;
  }

  @Override
  public void write(Element element) throws CacheException {
    writeBehindClient.incrementWriteCount();
    System.err.println("[WriteBehindCacheWriter written " + writeBehindClient.getWriteCount() + " for "
                       + writeBehindClient.getClass().getName() + "]");
    try {
      Thread.sleep(writeBehindClient.getSleepBetweenWrites());
    } catch (InterruptedException e) {
      // no-op
    }
  }

  @Override
  public void writeAll(Collection<Element> elements) throws CacheException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(CacheEntry entry) throws CacheException {
    writeBehindClient.incrementDeleteCount();
    System.err.println("[WriteBehindCacheWriter deleted " + writeBehindClient.getDeleteCount() + " for "
                       + writeBehindClient.getClass().getName() + "]");
    try {
      Thread.sleep(writeBehindClient.getSleepBetweenDeletes());
    } catch (InterruptedException e) {
      // no-op
    }
  }
}