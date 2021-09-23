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
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.Cache;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;

import org.junit.Assert;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.ehcache.tests.ClientBase;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import java.util.Collection;

public class ClusterTopologyTest extends AbstractCacheTestBase {

  public ClusterTopologyTest(TestConfig testConfig) {
    super("clustered-events-test.xml", testConfig, App.class, App.class, App.class);
  }

  public static class App extends ClientBase {

    public App(String[] args) {
      super(args);
    }

    @Override
    protected void runTest(Cache cache, Toolkit clusteringToolkit) throws Throwable {
      getBarrierForAllClients().await();

      CacheCluster cluster = cacheManager.getCluster(ClusterScheme.TERRACOTTA);
      Assert.assertTrue(cluster != null);
      Assert.assertTrue(cluster.getScheme().equals(ClusterScheme.TERRACOTTA));
      getBarrierForAllClients().await();
      Collection<ClusterNode> nodes = cluster.getNodes();
      // There will be 2 clients per node. ( 1 for cache+ 1 for toolkit)
      Assert.assertEquals(getParticipantCount() * 2, nodes.size());
      getBarrierForAllClients().await();
    }

  }
}
