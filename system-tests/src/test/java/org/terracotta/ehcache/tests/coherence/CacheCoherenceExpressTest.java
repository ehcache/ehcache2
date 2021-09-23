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
package org.terracotta.ehcache.tests.coherence;

import org.terracotta.ehcache.tests.AbstractCacheTestBase;

import com.tc.test.config.model.TestConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CacheCoherenceExpressTest extends AbstractCacheTestBase {

  public static final int CLIENT_COUNT = 3;

  public CacheCoherenceExpressTest(TestConfig testConfig) {
    super("cache-coherence-test.xml", testConfig, CacheCoherenceExpressClient.class, CacheCoherenceExpressClient.class,
          CacheCoherenceExpressClient.class);
    testConfig.addTcProperty("ehcache.evictor.logging.enabled", "true");
  }

  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File clientOutput) throws Throwable {
    super.evaluateClientOutput(clientName, exitCode, clientOutput);

    FileReader fr = null;
    try {
      fr = new FileReader(clientOutput);
      BufferedReader reader = new BufferedReader(fr);
      String st = "";
      while ((st = reader.readLine()) != null) {
        if (st.contains(CacheCoherenceExpressClient.PASS_OUTPUT)) return;
      }
      throw new AssertionError("Client exited without pass output string: " + CacheCoherenceExpressClient.PASS_OUTPUT);
    } catch (Exception e) {
      throw new AssertionError(e);
    } finally {
      try {
        fr.close();
      } catch (Exception e) {
        //
      }
    }
  }
}
