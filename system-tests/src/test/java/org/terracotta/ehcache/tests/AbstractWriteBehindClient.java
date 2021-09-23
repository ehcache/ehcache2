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

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractWriteBehindClient extends ClientBase {
  private final AtomicLong writeCount = new AtomicLong();
  private final AtomicLong deleteCount = new AtomicLong();

  public AbstractWriteBehindClient(String[] args) {
    super("test", args);
  }

  public void incrementWriteCount() {
    writeCount.incrementAndGet();
  }

  public void resetWriteCount() {
    writeCount.set(0);
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

  public abstract long getSleepBetweenWrites();
  public abstract long getSleepBetweenDeletes();
}