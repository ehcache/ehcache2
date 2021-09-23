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

import com.tc.test.config.model.TestConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ExpiryListenerTest extends AbstractCacheTestBase {

  public ExpiryListenerTest(TestConfig testConfig) {
    // assume the 'test' cache TTL is 3s
    super("evict-cache-test.xml", testConfig, ExpiryListenerClient1.class, ExpiryListenerClient2.class);
    testConfig.getClientConfig().setParallelClients(false);
  }

  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File output) throws Throwable {
    if ((exitCode != 0)) { throw new AssertionError("Client " + clientName + " exited with exit code: " + exitCode); }

    if (!ExpiryListenerClient1.class.getName().equals(clientName)) return;

    FileReader fr = new FileReader(output);
    BufferedReader reader = new BufferedReader(fr);
    try {
      String st;
      while ((st = reader.readLine()) != null) {
        if (st.contains("Got evicted")) return;
      }
      throw new AssertionError("Expecting eviction notice from client " + clientName);
    } catch (Exception e) {
      throw new AssertionError(e);
    } finally {
      try {
        reader.close();
      } catch (Exception e) {
        //
      }
    }
  }
}
