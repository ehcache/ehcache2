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

package net.sf.ehcache.management.sampled;

/**
 * An MBean for {@link CacheManager} exposing sampled cache usage statistics
 * 
 * <p />
 * 
 * @author <a href="mailto:asanoujam@terracottatech.com">Abhishek Sanoujam</a>
 * @since 1.7
 */
public interface SampledCacheManagerMBean {

    /**
     * Gets the status attribute of the Ehcache
     * 
     * @return The status value, as a String from the Status enum class
     */
    public String getStatus();

    /**
     * Shuts down the CacheManager.
     * <p/>
     * If the shutdown occurs on the singleton, then the singleton is removed,
     * so that if a singleton access method is called, a new singleton will be
     * created.
     */
    public void shutdown();

    /**
     * Clears the contents of all caches in the CacheManager, but without
     * removing any caches.
     * <p/>
     * This method is not synchronized. It only guarantees to clear those
     * elements in a cache at the time that the
     * {@link net.sf.ehcache.Ehcache#removeAll()} mehod on each cache is called.
     */
    public void clearAll();

    /**
     * Gets the cache names managed by the CacheManager
     */
    public String[] getCacheNames() throws IllegalStateException;

}
