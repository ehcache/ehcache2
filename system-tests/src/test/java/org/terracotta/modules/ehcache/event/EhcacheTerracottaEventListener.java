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
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EhcacheTerracottaEventListener implements CacheEventListener {
  private final List<Element> put       = new CopyOnWriteArrayList<Element>();
  private final List<Element> update    = new CopyOnWriteArrayList<Element>();
  private final List<Element> remove    = new CopyOnWriteArrayList<Element>();
  private final List<Element> expired   = new CopyOnWriteArrayList<Element>();
  private final List<Element> evicted   = new CopyOnWriteArrayList<Element>();
  private int                 removeAll = 0;

  public List<Element> getPut() {
    return put;
  }

  public List<Element> getUpdate() {
    return update;
  }

  public List<Element> getRemove() {
    return remove;
  }

  public List<Element> getExpired() {
    return expired;
  }

  public List<Element> getEvicted() {
    return evicted;
  }

  public int getRemoveAll() {
    return removeAll;
  }

  public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    put.add(element);
  }

  public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    update.add(element);
  }

  public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    remove.add(element);
  }

  public void notifyElementExpired(Ehcache cache, Element element) {
    expired.add(element);
  }

  public void notifyElementEvicted(Ehcache cache, Element element) {
    evicted.add(element);
  }

  public void notifyRemoveAll(Ehcache cache) {
    removeAll++;
  }

  public void dispose() {
    // no-op
  }

  @Override
  public EhcacheTerracottaEventListener clone() {
    throw new UnsupportedOperationException();
  }
}
