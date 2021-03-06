package net.sf.ehcache.management.service;

import net.sf.ehcache.management.resource.CacheEntityV2;

import org.terracotta.management.ServiceExecutionException;

/**
 * An interface for service implementations providing operations on Cache objects.
 *
 * @author brandony
 */
public interface CacheServiceV2 {

  /**
   * Create or update a cache represented by the submitted entity.
   *
   * @param cacheManagerName the name of the CacheManager managing the Cache to be updated
   * @param cacheName        the name of the Cache to be updated
   * @param resource         the representation of the resource informing this update
   * @throws ServiceExecutionException if the update fails
   */
  void createOrUpdateCache(String cacheManagerName,
                           String cacheName,
                           CacheEntityV2 resource) throws ServiceExecutionException;

  /**
   * Clears all the elements in the cache.
   *
   * @param cacheManagerName the name of the CacheManager managing the Cache to be cleared
   * @param cacheName        the name of the Cache to be cleared
   */
  void clearCache(String cacheManagerName,
                  String cacheName) throws ServiceExecutionException;
}
