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

public class SystemPropTcConfigTest extends AbstractCacheTestBase {

  public SystemPropTcConfigTest(TestConfig testConfig) {
    super("basic-cache-test.xml", testConfig);

    System.out.println("adding extra -Dtc.config");
    testConfig.getClientConfig().addExtraClientJvmArg("-Dtc.config=tc-config.xml");
  }

  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File output) throws Throwable {
    if ((exitCode == 0)) { throw new AssertionError("Client " + clientName + " exited with exit code: " + exitCode); }

    FileReader fr = null;
    boolean containsException = false;
    boolean containsErrorMsg = false;
    String errorMessage = "The Terracotta config file should not be set through -Dtc.config in this usage.";
    try {
      fr = new FileReader(output);
      BufferedReader reader = new BufferedReader(fr);
      String st = "";
      while ((st = reader.readLine()) != null) {
        if (st.contains("net.sf.ehcache.CacheException")) containsException = true;
        if (st.contains(errorMessage)) containsErrorMsg = true;
      }
    } catch (Exception e) {
      throw new AssertionError(e);
    } finally {
      try {
        fr.close();
      } catch (Exception e) {
        //
      }
    }

    if (!containsException) { throw new AssertionError(
                                                       "Expecting client to fail with exception: net.sf.ehcache.CacheException"); }

    if (!containsErrorMsg) { throw new AssertionError("Expecting client to fail with message: " + errorMessage); }
  }

}
