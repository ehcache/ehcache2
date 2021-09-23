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
package org.terracotta.modules.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheAccessor;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.Store;

import org.terracotta.toolkit.Toolkit;

public class ToolkitClientAccessor {

  public static Toolkit getInternalToolkitClient(Cache cache) {
    CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
    if (cacheConfiguration.isTerracottaClustered()) {
      CacheAccessor storeAccessor = CacheAccessor.newCacheAccessor(cache);
      Store store = null;
      store = storeAccessor.getStore();
      Object internalContext = store.getInternalContext();
      if (internalContext instanceof ToolkitLookup) {
        return ((ToolkitLookup) internalContext).getToolkit();
      } else {
        throw new AssertionError("Internal context of cache '" + cache.getName() + "' is not of type ToolkitLookup");
      }
    } else {
      throw new AssertionError("Toolkit can only be looked up for Clustered Caches - unclustered ehcache name: "
                               + cache.getName());
    }
  }

}
