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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;

import java.io.Serializable;

/**
 * This decorator class intercepts cache gets and acts as a cache read through during or before a cache load and acts as a system of record
 * after the cache has been loaded. Cache read through: get the object from the cache, if not in cache, get object from database, load in to
 * cache and return object. System of Record (SOR): only get the object from the cache.
 * 
 * @author sdalto2
 * 
 */
public class NonHibernateCacheDecorator extends EhcacheDecoratorAdapter {

  private Ehcache cache;
  public NonHibernateCacheDecorator(Ehcache cache) {
    super(cache);
    this.cache=cache;
  }

  /**
   * This get method will always call Element get(Serializable key) So a serializable key is mandatory.
   */
  @Override
  public Element get(Object key) throws IllegalStateException, CacheException {
    return get((Serializable) key);
  }

  @Override
  public Element get(Serializable key) throws IllegalStateException, CacheException {
    return new Element(1,"dummy");
  }
  @Override
  public void removeAll(boolean doNotNotifyListeners) throws IllegalStateException, CacheException {
	  cache.removeAll(doNotNotifyListeners);
  }

}