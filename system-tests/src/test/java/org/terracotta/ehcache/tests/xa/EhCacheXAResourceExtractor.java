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
package org.terracotta.ehcache.tests.xa;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheStoreHelper;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaTransactionalCopyingCacheStore;
import net.sf.ehcache.transaction.xa.XATransactionStore;

import org.terracotta.modules.ehcache.store.nonstop.NonStopStoreWrapper;

import java.lang.reflect.Field;

import javax.transaction.xa.XAResource;

public class EhCacheXAResourceExtractor {

  public static XAResource extractXAResource(Cache cache) {
    try {
      CacheStoreHelper helper = new CacheStoreHelper(cache);
      Store store = helper.getStore();
      if (store instanceof NonStopStoreWrapper) {
        store = (Store) getPrivateField(store, "delegate");
      }
      return ((XATransactionStore) ((TerracottaTransactionalCopyingCacheStore)store).getUnderlyingStore()).getOrCreateXAResource();
    } catch (Exception e) {
      throw new RuntimeException("cannot extract XAResource out of cache " + cache, e);
    }
  }

  private static Object getPrivateField(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    Field field = o.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(o);
  }

}
