/**
 *  Copyright 2003-2009 Terracotta, Inc.
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
package net.sf.ehcache.statistics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Implementation which can be used both as a {@link CacheUsageStatistics} and
 * {@link CacheUsageStatisticsData}
 * 
 * @author <a href="mailto:asanoujam@terracottatech.com">Abhishek Sanoujam</a>
 * @since 1.7
 */
public class CacheUsageStatisticsImpl implements CacheUsageStatistics,
        CacheUsageStatisticsData {

    private final AtomicBoolean statisticsEnabled = new AtomicBoolean();
    private final AtomicLong cacheHitInMemoryCount = new AtomicLong();
    private final AtomicLong cacheHitOnDiskCount = new AtomicLong();
    private final AtomicLong cacheMissNotFound = new AtomicLong();
    private final AtomicLong cacheMissExpired = new AtomicLong();
    private final AtomicLong cacheElementEvictedCount = new AtomicLong();
    private final AtomicLong totalGetTimeTakenMillis = new AtomicLong();
    private final AtomicLong cacheElementRemoved = new AtomicLong();
    private final AtomicLong cacheElementExpired = new AtomicLong();
    private final AtomicLong cacheElementPut = new AtomicLong();
    private final AtomicLong cacheElementUpdated = new AtomicLong();
    private final AtomicInteger statisticsAccuracy = new AtomicInteger();

    private final List<CacheUsageListener> listeners = new CopyOnWriteArrayList<CacheUsageListener>();

    private final Ehcache cache;

    /**
     * Constructor that accepts the backing {@link Ehcache}, needed for
     * {@link #getSize()}
     * 
     * @param cache
     */
    public CacheUsageStatisticsImpl(Ehcache cache) {
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    public void clearStatistics() {
        cacheHitInMemoryCount.set(0);
        cacheHitOnDiskCount.set(0);
        cacheMissExpired.set(0);
        cacheMissNotFound.set(0);
        cacheElementEvictedCount.set(0);
        totalGetTimeTakenMillis.set(0);
        for (CacheUsageListener l : listeners) {
            l.notifyStatisticsCleared();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStatisticsEnabled() {
        return statisticsEnabled.get();
    }

    /**
     * {@inheritDoc}
     */
    public void setStatisticsEnabled(boolean enableStatistics) {
        if (enableStatistics) {
            clearStatistics();
        }
        statisticsEnabled.set(enableStatistics);
        for (CacheUsageListener l : listeners) {
            l.notifyStatisticsEnabledChanged(enableStatistics);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addGetTimeMillis(int millis) {
        if (!statisticsEnabled.get()) {
            return;
        }
        totalGetTimeTakenMillis.addAndGet(millis);
        for (CacheUsageListener l : listeners) {
            l.notifyTimeTakenForGet(millis);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cacheElementEvicted() {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementEvictedCount.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementEvicted();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cacheHitInMemory() {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheHitInMemoryCount.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheHitInMemory();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cacheHitOnDisk() {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheHitOnDiskCount.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheHitOnDisk();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cacheMissExpired() {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheMissExpired.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheMissedWithExpired();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cacheMissNotFound() {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheMissNotFound.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheMissedWithNotFound();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setStatisticsAccuracy(int statisticsAccuracy) {
        this.statisticsAccuracy.set(statisticsAccuracy);
        for (CacheUsageListener l : listeners) {
            l.notifyStatisticsAccuracyChanged(statisticsAccuracy);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        for (CacheUsageListener l : listeners) {
            l.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementEvicted(Ehcache cache, Element element) {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementEvictedCount.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementEvicted();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementExpired(Ehcache cache, Element element) {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementExpired.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementExpired();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementPut(Ehcache cache, Element element)
            throws CacheException {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementPut.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementPut();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementRemoved(Ehcache cache, Element element)
            throws CacheException {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementRemoved.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementRemoved();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementUpdated(Ehcache cache, Element element)
            throws CacheException {
        if (!statisticsEnabled.get()) {
            return;
        }
        cacheElementUpdated.incrementAndGet();
        for (CacheUsageListener l : listeners) {
            l.notifyCacheElementUpdated();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notifyRemoveAll(Ehcache cache) {
        if (!statisticsEnabled.get()) {
            return;
        }
        for (CacheUsageListener l : listeners) {
            l.notifyRemoveAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // to shut up check-style, why do we need this ?
        super.clone();
        throw new CloneNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    public float getAverageGetTimeMillis() {
        long hitCount = getCacheHitCount();
        if (hitCount == 0) {
            return 0f;
        }
        return (float) totalGetTimeTakenMillis.get() / hitCount;
    }

    /**
     * {@inheritDoc}
     */
    public void registerCacheUsageListener(CacheUsageListener cacheUsageListener)
            throws IllegalStateException {
        listeners.add(cacheUsageListener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeCacheUsageListener(CacheUsageListener cacheUsageListener)
            throws IllegalStateException {
        listeners.remove(cacheUsageListener);
    }

    /**
     * {@inheritDoc}
     */
    public long getCacheHitCount() {
        return cacheHitInMemoryCount.get() + cacheHitOnDiskCount.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getCacheMissCount() {
        return cacheMissExpired.get() + cacheMissNotFound.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getCacheMissCountExpired() {
        return cacheMissExpired.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getCacheMissCountNotFound() {
        return cacheMissNotFound.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getEvictedCount() {
        return cacheElementEvictedCount.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getInMemoryHitCount() {
        return cacheHitInMemoryCount.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getOnDiskHitCount() {
        return cacheHitOnDiskCount.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getSize() {
        return cache.getSizeBasedOnAccuracy(statisticsAccuracy.get());
    }

    /**
     * {@inheritDoc}
     */
    public long getInMemorySize() {
        return cache.getMemoryStoreSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getOnDiskSize() {
        return cache.getDiskStoreSize();
    }

    /**
     * {@inheritDoc}
     */
    public String getAssociatedCacheName() {
        return cache.getName();
    }

    /**
     * {@inheritDoc}
     */
    public int getStatisticsAccuracy() {
        return statisticsAccuracy.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getExpiredCount() {
        return cacheElementExpired.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getPutCount() {
        return cacheElementPut.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getRemovedCount() {
        return cacheElementRemoved.get();
    }

    /**
     * {@inheritDoc}
     */
    public long getUpdateCount() {
        return cacheElementUpdated.get();
    }

    /**
     * {@inheritDoc}
     */
    public String getStatisticsAccuracyDescription() {
        if (statisticsAccuracy.get() == 0) {
            return "None";
        } else if (statisticsAccuracy.get() == 1) {
            return "Best Effort";
        } else {
            return "Guaranteed";
        }
    }

}
