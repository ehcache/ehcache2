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

/**
 * @author cdennis
 */
public class OverflowToDiskStandaloneCacheTest extends AbstractCacheTestBase {

  public OverflowToDiskStandaloneCacheTest(TestConfig testConfig) {
    super("overflow-to-disk-cache-test.xml", testConfig);
  }

  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File output) throws Throwable {
    if ((exitCode == 0)) { throw new AssertionError("Client " + clientName + " exited with exit code: " + exitCode); }

    FileReader fr = null;
    try {
      fr = new FileReader(output);
      BufferedReader reader = new BufferedReader(fr);
      String st = "";
      while ((st = reader.readLine()) != null) {
        if (st.contains("InvalidConfigurationException")) return;
      }
      throw new AssertionError("Client " + clientName + " did not pass");
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
