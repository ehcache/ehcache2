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
package org.terracotta.ehcache.tests;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;

import java.util.Properties;

public class NonHibernateCacheDecoratorFactory extends CacheDecoratorFactory {

  // private static final String DAO_NAME = "daoName";

  @Override
  public Ehcache createDecoratedEhcache(Ehcache cache, Properties properties) {
    NonHibernateCacheDecorator cacheDecorator = new NonHibernateCacheDecorator(cache);
    return cacheDecorator;
  }

  @Override
  public Ehcache createDefaultDecoratedEhcache(Ehcache cache, Properties properties) {
    NonHibernateCacheDecorator cacheDecorator = new NonHibernateCacheDecorator(cache);
    return cacheDecorator;
  }

}
