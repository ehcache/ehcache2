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

import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class DummyTransaction implements Transaction {

  private final long id;

  public DummyTransaction(long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DummyTransaction) {
      DummyTransaction otherTx = (DummyTransaction) o;
      return otherTx.id == id;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (int) id;
  }

  public void commit() throws SecurityException {
    //
  }

  public boolean delistResource(XAResource xaResource, int i) throws IllegalStateException {
    return true;
  }

  public boolean enlistResource(XAResource xaResource) throws IllegalStateException {
    return true;
  }

  public int getStatus() {
    return 0;
  }

  public void registerSynchronization(Synchronization synchronization) throws IllegalStateException {
    //
  }

  public void rollback() throws IllegalStateException {
    //
  }

  public void setRollbackOnly() throws IllegalStateException {
    //
  }
}
