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
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.terracotta.ehcache.tests.container.hibernate.BaseClusteredRegionFactoryTestServlet;
import org.terracotta.ehcache.tests.container.hibernate.domain.Item;

import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

public class NonEternalSecondLevelCacheTestServlet extends BaseClusteredRegionFactoryTestServlet {

  @Override
  protected void doServer0(HttpSession session, Map<String, String[]> parameters) throws Exception {
    HibernateUtil.dropAndCreateDatabaseSchema();

    Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
    stats.setStatisticsEnabled(true);
    SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName());

    Session s = HibernateUtil.getSessionFactory().openSession();
    Transaction t = s.beginTransaction();
    Item i = new Item();
    i.setName("widget");
    i.setDescription("A really top-quality, full-featured widget.");
    s.persist(i);
    long id = i.getId();
    t.commit();
    s.close();

    Assert.assertEquals(1, statistics.getPutCount());
    Assert.assertEquals(0, statistics.getHitCount());

    s = HibernateUtil.getSessionFactory().openSession();
    t = s.beginTransaction();
    s.get(Item.class, id);
    t.commit();
    s.close();

    Assert.assertEquals(1, statistics.getPutCount());
    Assert.assertEquals(1, statistics.getHitCount());

    Thread.sleep(15000);

    s = HibernateUtil.getSessionFactory().openSession();
    t = s.beginTransaction();
    s.get(Item.class, id);
    t.commit();
    s.close();

    Assert.assertEquals(2, statistics.getPutCount());
    Assert.assertEquals(1, statistics.getHitCount());

    s = HibernateUtil.getSessionFactory().openSession();
    t = s.beginTransaction();
    s.get(Item.class, id);
    t.commit();
    s.close();

    Assert.assertEquals(2, statistics.getPutCount());
    Assert.assertEquals(2, statistics.getHitCount());
  }

  @Override
  protected void doServer1(HttpSession session, Map<String, String[]> parameters) throws Exception {
    Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
    SecondLevelCacheStatistics cacheStats = stats.getSecondLevelCacheStatistics(Item.class.getName());

    HibernateUtil.getSessionFactory().evictEntity(Item.class.getName());

    Assert.assertEquals(0L, cacheStats.getElementCountInMemory());
  }
}
