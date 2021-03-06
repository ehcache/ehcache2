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
package org.terracotta.ehcache.tests.container.hibernate.nontransactional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.terracotta.ehcache.tests.container.hibernate.BaseClusteredRegionFactoryTestServlet;
import org.terracotta.ehcache.tests.container.hibernate.domain.VersionedItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class StaleWritesLeaveCacheConsistentServlet extends BaseClusteredRegionFactoryTestServlet {

  @Override
  protected void doServer0(HttpSession session, Map<String, String[]> parameters) throws Exception {
    HibernateUtil.dropAndCreateDatabaseSchema();

    Session s = openSession();
    Transaction txn = s.beginTransaction();
    VersionedItem item = new VersionedItem();
    item.setName("chris");
    item.setDescription("chris' item");
    s.save(item);
    txn.commit();
    s.close();

    Long initialVersion = item.getVersion();

    // manually revert the version property
    item.setVersion(Long.valueOf(initialVersion.longValue() - 1));
    item.setName("tim");
    item.setDescription("tim's item");
    s = openSession();
    try {
      txn = s.beginTransaction();
      try {
        s.update(item);
        txn.commit();
        s.close();
        throw new Exception("expected stale write to fail");
      } catch (Throwable expected) {
        txn.rollback();
      }
    } finally {
      if (s.isOpen()) {
        s.close();
      }
    }

    // check the version value in the cache...
    s = openSession();
    txn = s.beginTransaction();
    VersionedItem check = (VersionedItem) s.get(VersionedItem.class, item.getId());
    Assert.assertEquals(initialVersion, check.getVersion());
    txn.commit();
    s.close();
  }

  @Override
  protected void doServer1(HttpSession session, Map<String, String[]> parameters) throws Exception {
    // check the version value in the cache...
    Session s = openSession();
    Transaction txn = s.beginTransaction();
    List<Long> ids = s.createQuery("select id from VersionedItem").list();
    for (Long id : ids) {
      VersionedItem item = (VersionedItem) s.get(VersionedItem.class, id);
      Assert.assertEquals("chris", item.getName());
    }
    txn.commit();
    s.close();
  }

  private Session openSession() {
    return HibernateUtil.getSessionFactory().openSession();
  }

}
