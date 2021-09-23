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

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.AbstractCacheWriter;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class WriteBehindCacheWriter extends AbstractCacheWriter {
  private final String     name;
  private final int        ClientId;
  private final long       sleepBetweenOperations;
  private final AtomicLong writeCount  = new AtomicLong();
  private final AtomicLong deleteCount = new AtomicLong();
  private final boolean    silent;

  public void incrementWriteCount() {
    writeCount.incrementAndGet();
  }

  public long getWriteCount() {
    return writeCount.longValue();
  }

  public void incrementDeleteCount() {
    deleteCount.incrementAndGet();
  }

  public long getDeleteCount() {
    return deleteCount.longValue();
  }

  public WriteBehindCacheWriter(String name, int ClientId, long sleepBetweenOperations) {
    this(name, ClientId, sleepBetweenOperations, false);
  }

  public WriteBehindCacheWriter(String name, int clientId, long sleepBetweenOperations, boolean silent) {
    this.name = name;
    this.ClientId = clientId;
    this.sleepBetweenOperations = sleepBetweenOperations;
    this.silent = silent;
  }

  @Override
  public void write(Element element) throws CacheException {
    incrementWriteCount();
    if (!silent) {
      System.err.println("[" + name + " written " + getWriteCount() + " for " + this.name + " " + this.ClientId + "]");
    }
    try {
      Thread.sleep(sleepBetweenOperations);
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
    incrementDeleteCount();
    System.err.println("[" + name + " deleted " + getDeleteCount() + " for " + this.name + " " + this.ClientId + "]");
    try {
      Thread.sleep(sleepBetweenOperations);
    } catch (InterruptedException e) {
      // no-op
    }
  }

  @Override
  public void dispose() throws CacheException {
    System.err.println("Shutting " + this.getClass().getSimpleName() + " named '" + name + "' down...");
  }
}
