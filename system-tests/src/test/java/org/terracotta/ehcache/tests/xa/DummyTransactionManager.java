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

import java.util.concurrent.atomic.AtomicLong;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public class DummyTransactionManager implements TransactionManager {

  private final AtomicLong txIdGenerator = new AtomicLong();

  private DummyTransaction testTransaction;

  public DummyTransactionManager() {
    //
  }

  public void begin() {
    testTransaction = new DummyTransaction(txIdGenerator.incrementAndGet());
  }

  public void commit() throws IllegalStateException, SecurityException {
    //
  }

  public int getStatus() {
    return 0;
  }

  public Transaction getTransaction() {
    return testTransaction;
  }

  public void resume(Transaction transaction) throws IllegalStateException {
    testTransaction = (DummyTransaction) transaction;
  }

  public void rollback() throws IllegalStateException, SecurityException {
    //
  }

  public void setRollbackOnly() throws IllegalStateException {
    //
  }

  public void setTransactionTimeout(int i) {
    //
  }

  public Transaction suspend() {
    DummyTransaction suspendedTx = testTransaction;
    testTransaction = null;
    return suspendedTx;
  }
}
