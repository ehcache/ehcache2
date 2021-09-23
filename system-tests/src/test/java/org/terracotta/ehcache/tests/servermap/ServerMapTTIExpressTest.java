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
package org.terracotta.ehcache.tests.servermap;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;

import com.tc.test.config.model.TestConfig;

import java.util.Iterator;

public class ServerMapTTIExpressTest extends AbstractCacheTestBase {

  public ServerMapTTIExpressTest(TestConfig testConfig) {
    super("/servermap/basic-servermap-cache-test.xml", testConfig, ServerMapTTIExpressTestClient.class);
    testConfig.setDgcEnabled(true);
    testConfig.setDgcIntervalInSec(60);
    testConfig.addTcProperty("ehcache.evictor.logging.enabled", "true");
    testConfig.getL2Config().addExtraServerJvmArg("-Dcom.tc.ehcache.evictor.logging.enabled=true");
    testConfig.getL2Config()
        .addExtraServerJvmArg("-Dcom.tc.ehcache.storageStrategy.dcv2.perElementTTITTL.enabled=true");
    testConfig.getL2Config()
        .addExtraServerJvmArg("-Dcom.tc.ehcache.storageStrategy.dcv2.evictUnexpiredEntries.enabled=false");

    final Iterator<String> iter = testConfig.getClientConfig().getExtraClientJvmArgs().iterator();
    while (iter.hasNext()) {
      final String prop = iter.next();
      if (prop.contains("ehcache.storageStrategy.dcv2.localcache.enabled")) {
        // remove it and always disable localcache for this test
        iter.remove();
      }
    }
    // always disable local cache
    testConfig.getClientConfig().addExtraClientJvmArg("-Dcom.tc.ehcache.storageStrategy.dcv2.localcache.enabled=false");
  }

}
