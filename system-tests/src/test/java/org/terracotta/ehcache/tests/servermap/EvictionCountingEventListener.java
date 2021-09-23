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
package org.terracotta.ehcache.tests.servermap;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.terracotta.toolkit.concurrent.atomic.ToolkitAtomicLong;

public class EvictionCountingEventListener implements CacheEventListener {

  private final ToolkitAtomicLong count;

  public EvictionCountingEventListener(ToolkitAtomicLong clusteredAtomicLong) {
    this.count = clusteredAtomicLong;
  }

  public void notifyElementEvicted(Ehcache cache, Element element) {
    long val = count.incrementAndGet();
    if (val % 100 == 0) {
      System.out.println("EvictionListener: number of elements evicted till now: " + val);
    }
  }

  public void dispose() {
    //
  }

  public void notifyElementExpired(Ehcache cache, Element element) {
    //
  }

  public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    //
  }

  public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    //
  }

  public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    //
  }

  public void notifyRemoveAll(Ehcache cache) {
    //
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public long getEvictedCount() {
    return this.count.get();
  }

}
