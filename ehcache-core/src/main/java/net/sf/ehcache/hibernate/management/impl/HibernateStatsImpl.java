/**
 *  Copyright Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.hibernate.management.impl;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import net.sf.ehcache.hibernate.management.api.HibernateStats;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

/**
 * Implementation of {@link HibernateStats}
 * 
 * <p>
 * 
 * @author <a href="mailto:asanoujam@terracottatech.com">Abhishek Sanoujam</a>
 * 
 */
public class HibernateStatsImpl extends BaseEmitterBean implements HibernateStats {
    private static final double MILLIS_PER_SECOND = 1000;
    private static final MBeanNotificationInfo NOTIFICATION_INFO;

    private final SessionFactory sessionFactory;

    static {
        final String[] notifTypes = new String[] {};
        final String name = Notification.class.getName();
        final String description = "Hibernate Statistics Event";
        NOTIFICATION_INFO = new MBeanNotificationInfo(notifTypes, name, description);
    }

    /**
     * Constructor accepting the backing {@link SessionFactory}
     * 
     * @param sessionFactory
     * @throws NotCompliantMBeanException 
     */
    public HibernateStatsImpl(SessionFactory sessionFactory) throws NotCompliantMBeanException {
        super(HibernateStats.class);
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return statistics
     */
    private Statistics getStatistics() {
        return sessionFactory.getStatistics();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#clearStats()
     */
    public void clearStats() {
        getStatistics().clear();
        sendNotification(CACHE_STATISTICS_RESET);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#disableStats()
     */
    public void disableStats() {
        setStatisticsEnabled(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#enableStats()
     */
    public void enableStats() {
        setStatisticsEnabled(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getCloseStatementCount()
     */
    public long getCloseStatementCount() {
        return getStatistics().getCloseStatementCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getConnectCount()
     */
    public long getConnectCount() {
        return getStatistics().getConnectCount();
    }

    /**
     * Not supported right now
     * 
     */
    public long getDBSQLExecutionSample() {
        throw new UnsupportedOperationException("Use getQueryExecutionCount() instead");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getFlushCount()
     */
    public long getFlushCount() {
        return getStatistics().getFlushCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getOptimisticFailureCount()
     */
    public long getOptimisticFailureCount() {
        return getStatistics().getOptimisticFailureCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getPrepareStatementCount()
     */
    public long getPrepareStatementCount() {
        return getStatistics().getPrepareStatementCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getQueryExecutionCount()
     */
    public long getQueryExecutionCount() {
        return getStatistics().getQueryExecutionCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getQueryExecutionRate()
     */
    public double getQueryExecutionRate() {
        long startTime = getStatistics().getStartTime();
        long now = System.currentTimeMillis();
        double deltaSecs = (now - startTime) / MILLIS_PER_SECOND;
        return getQueryExecutionCount() / deltaSecs;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getQueryExecutionSample()
     */
    public long getQueryExecutionSample() {
        throw new UnsupportedOperationException("TODO: need to impl. rates for query execution");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getSessionCloseCount()
     */
    public long getSessionCloseCount() {
        return getStatistics().getSessionCloseCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getSessionOpenCount()
     */
    public long getSessionOpenCount() {
        return getStatistics().getSessionOpenCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getSuccessfulTransactionCount()
     */
    public long getSuccessfulTransactionCount() {
        return getStatistics().getSuccessfulTransactionCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getTransactionCount()
     */
    public long getTransactionCount() {
        return getStatistics().getTransactionCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#isStatisticsEnabled()
     */
    public boolean isStatisticsEnabled() {
        return getStatistics().isStatisticsEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#setStatisticsEnabled(boolean)
     */
    public void setStatisticsEnabled(boolean flag) {
        getStatistics().setStatisticsEnabled(flag);
        sendNotification(CACHE_STATISTICS_ENABLED, flag);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getEntityStats()
     */
    public TabularData getEntityStats() {
        List<CompositeData> result = new ArrayList<CompositeData>();
        Statistics statistics = getStatistics();
        for (String entity : statistics.getEntityNames()) {
            EntityStats entityStats = new EntityStats(entity, statistics.getEntityStatistics(entity));
            result.add(entityStats.toCompositeData());
        }
        TabularData td = EntityStats.newTabularDataInstance();
        td.putAll(result.toArray(new CompositeData[result.size()]));
        return td;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getCollectionStats()
     */
    public TabularData getCollectionStats() {
        List<CompositeData> result = new ArrayList<CompositeData>();
        Statistics statistics = getStatistics();
        for (String roleName : statistics.getCollectionRoleNames()) {
            CollectionStats collectionStats = new CollectionStats(roleName, statistics.getCollectionStatistics(roleName));
            result.add(collectionStats.toCompositeData());
        }
        TabularData td = CollectionStats.newTabularDataInstance();
        td.putAll(result.toArray(new CompositeData[result.size()]));
        return td;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sf.ehcache.hibernate.management.api.HibernateStats#getQueryStats()
     */
    public TabularData getQueryStats() {
        List<CompositeData> result = new ArrayList<CompositeData>();
        Statistics statistics = getStatistics();
        for (String query : statistics.getQueries()) {
            QueryStats queryStats = new QueryStats(query, statistics.getQueryStatistics(query));
            result.add(queryStats.toCompositeData());
        }
        TabularData td = QueryStats.newTabularDataInstance();
        td.putAll(result.toArray(new CompositeData[result.size()]));
        return td;
    }

    /**
     * {@inheritDoc}
     */
    public TabularData getCacheRegionStats() {
        List<CompositeData> list = new ArrayList<CompositeData>();
        Statistics statistics = getStatistics();
        for (String region : statistics.getSecondLevelCacheRegionNames()) {
            CacheRegionStats l2CacheStats = new CacheRegionStats(region, statistics.getSecondLevelCacheStatistics(region));
            list.add(l2CacheStats.toCompositeData());
        }
        TabularData td = CacheRegionStats.newTabularDataInstance();
        td.putAll(list.toArray(new CompositeData[list.size()]));
        return td;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDispose() {
        // no-op
    }

    /**
     * @see BaseEmitterBean#getNotificationInfo()
     */
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{NOTIFICATION_INFO};
    }
}
