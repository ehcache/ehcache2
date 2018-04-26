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

package net.sf.ehcache.management;

/**
 * @author Greg Luck
 * @version $Id$
 * @since 1.3
 */
public interface CacheStatisticsMBean {

    /**
     * The number of times a requested item was found in the cache.
     *
     * @return the number of times a requested item was found in the cache
     */
    long getCacheHits();

    /**
     * Number of times a requested item was found in the Memory Store.
     *
     * @return the number of times a requested item was found in memory
     */
    long getInMemoryHits();

    /**
     * Number of times a requested item was found in the off-heap store.
     *
     * @return the number of times a requested item was found off-heap, or 0 if there is no BigMemory storage configured.
     */
    long getOffHeapHits();

    /**
     * Number of times a requested item was found in the Disk Store.
     *
     * @return the number of times a requested item was found on Disk, or 0 if there is no disk storage configured.
     */
    long getOnDiskHits();

    /**
     * @return the number of times a requested element was not found in the cache
     */
    long getCacheMisses();

    /**
     * @return the number of times a requested element was not found in the memory cache
     */
    long getInMemoryMisses();

    /**
     * @return the number of times a requested element was not found in the off-heap cache
     */
    long getOffHeapMisses();

    /**
     * @return the number of times a requested element was not found in the disk cache
     */
    long getOnDiskMisses();

    /**
     * @return the number of elements in the ehcache, with a varying degree of accuracy, depending on accuracy setting.
     */
    long getObjectCount();


    /**
     * Gets the number of objects in the MemoryStore
     * @return the MemoryStore size which is always a count unadjusted for duplicates or expiries
     */
    long getMemoryStoreObjectCount();

    /**
     * Gets the number of objects in the OffHeapStore
     * @return the OffHeapStore size which is always a count unadjusted for duplicates or expiries
     */
    long getOffHeapStoreObjectCount();

    /**
     * Gets the number of objects in the DiskStore
     * @return the DiskStore size which is always a count unadjusted for duplicates or expiries
     */
    long getDiskStoreObjectCount();

    /**
     * @return the name of the Ehcache, or null is there no associated cache
     */
    String getAssociatedCacheName();

    /**
     * Returns the percentage of cache accesses that found a requested item in the cache.
     *
     * @return the percentage of successful hits
     */
    double getCacheHitPercentage();

    /**
     * Returns the percentage of cache accesses that did not find a requested element in the cache.
     *
     * @return the percentage of accesses that failed to find anything
     */
    double getCacheMissPercentage();

    /**
     * Returns the percentage of cache accesses that found a requested item cached in-memory.
     *
     * @return the percentage of successful hits from the MemoryStore
     */
    double getInMemoryHitPercentage();

    /**
     * Returns the percentage of cache accesses that found a requested item cached off-heap.
     *
     * @return the percentage of successful hits from the OffHeapStore
     */
    double getOffHeapHitPercentage();

    /**
     * Returns the percentage of cache accesses that found a requested item cached on disk.
     *
     * @return the percentage of successful hits from the DiskStore.
     */
    double getOnDiskHitPercentage();

    /**
     * Gets the size of the write-behind queue, if any.
     * The value is for all local buckets
     * @return Elements waiting to be processed by the write behind writer. -1 if no write-behind
     */
    long getWriterQueueLength();

    /**
     * Gets the maximum size of the write-behind queue, if any.
     * @return Maximum elements waiting to be processed by the write behind writer. -1 if no write-behind
     */
    int getWriterMaxQueueSize();
}
