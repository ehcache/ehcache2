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
package org.terracotta.modules.ehcache.lifecycle;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;
import com.terracotta.entity.ClusteredEntityManager;
import com.terracotta.entity.ehcache.ClusteredCacheManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * CacheManagerListingTest
 */
public class CacheManagerListingTest extends AbstractCacheTestBase {
    public CacheManagerListingTest(TestConfig testConfig) {
        super("lifecycle/cache-manager-minimal.xml", testConfig, CacheManagerCreateClient.class, ClusteredEntityClient.class);
    }

    public static class CacheManagerCreateClient extends ClientBase {
        public CacheManagerCreateClient(String[] args) {
            super(args);
        }

        @Override
        protected void runTest(Cache cache, Toolkit myToolkit) throws Throwable {
            // Client setup already created CacheManager
            // Signalling for other client to check it can be listed
            getBarrierForAllClients().await(10, TimeUnit.SECONDS);

            // Waiting for other client to finish listing asserts
            getBarrierForAllClients().await(1, TimeUnit.MINUTES);
        }
    }

    public static class ClusteredEntityClient extends ClientBase {
        public ClusteredEntityClient(String[] args) {
            super(args);
        }

        @Override
        protected void setupCacheManager() {
            // Do nothing here
        }

        @Override
        protected Cache getCache() {
            // Do nothing here
            return null;
        }

        @Override
        protected void runTest(Cache cache, Toolkit myToolkit) throws Throwable {
            // Waiting for CM to be created
            getBarrierForAllClients().await(1, TimeUnit.MINUTES);

            ClusteredEntityManager clusteredEntityManager = new ClusteredEntityManager(myToolkit);
            Configuration configuration = ConfigurationFactory.parseConfiguration(getEhcacheXmlAsStream());
            Map<String,ClusteredCacheManager> cacheManagers = clusteredEntityManager.getRootEntities(ClusteredCacheManager.class);

            ClusteredCacheManager clusteredCacheManager = cacheManagers.get(configuration.getName());
            assertNotNull(clusteredCacheManager);

            getBarrierForAllClients().await(10, TimeUnit.SECONDS);
        }
    }
}
