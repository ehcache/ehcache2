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
package org.terracotta.ehcache.tests.txns;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.manager.DefaultTransactionManagerLookup;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;

import org.terracotta.toolkit.Toolkit;

import javax.transaction.Status;
import javax.transaction.TransactionManager;

public class TwoResourceTx2 extends AbstractTxClient {

  public TwoResourceTx2(String[] args) {
    super(args);
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    Cache cache2 = getCacheManager().getCache("test2");
    final TransactionManagerLookup lookup = new DefaultTransactionManagerLookup();

    final TransactionManager txnManager = lookup.getTransactionManager();

    getBarrierForAllClients().await();

    try {
      txnManager.begin();
      Element oldElement = cache.get("key1");
      if (!"value1".equals(oldElement.getValue())) { throw new AssertionError("Should have been put by Client 1"); }
      Element removedElement = cache.get("remove1");

      if (removedElement != null) { throw new AssertionError("remove1 key should not exist!"); }

      cache.put(new Element("key1", "value2"));
      cache2.put(new Element("key1", "value1"));

      System.out.println("\nReading entry");

      txnManager.commit();

      txnManager.begin();
      cache2.put(new Element("key1", "value1"));

      System.out.println("Value is: " + cache.get("key1").getValue());
      System.out.println("Size of is: " + cache.getSize());
      txnManager.commit();

    } catch (Exception e) {
      try {
        if (txnManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
          txnManager.rollback();
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      // Let root cause bubble up instead of failure to rollback
      throw e;
    }
  }

  public static void main(String[] args) {
    new TwoResourceTx2(args).run();
  }
}
