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

public class ConcurrencyValueTest extends AbstractCacheTestBase {
  private static final int CDM_DEFAULT_CONCURRENCY = 256;

  public ConcurrencyValueTest(TestConfig testConfig) {
    super("basic-cache-test.xml", testConfig, Client1.class);
  }

  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File clientOutput) throws Throwable {
    super.evaluateClientOutput(clientName, exitCode, clientOutput);

    FileReader fr = null;
    boolean currencyValueLogged1 = false;
    boolean currencyValueLogged2 = false;
    String currencyValueLogMsg1 = getConcurrencyValueLogMsg("defaultConcurrencyCache", CDM_DEFAULT_CONCURRENCY);
    String currencyValueLogMsg2 = getConcurrencyValueLogMsg("configuredConcurrencyCache", 123);
    try {
      fr = new FileReader(clientOutput);
      BufferedReader reader = new BufferedReader(fr);
      String st = "";
      while ((st = reader.readLine()) != null) {
        if (st.contains(currencyValueLogMsg1)) currencyValueLogged1 = true;
        if (st.contains(currencyValueLogMsg2)) currencyValueLogged2 = true;
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

    if (!currencyValueLogged1) { throw new AssertionError(); }

    if (!currencyValueLogged2) { throw new AssertionError(); }
  }

  private static String getConcurrencyValueLogMsg(String name, int concurrency) {
    return "Cache [" + name + "] using concurrency: " + concurrency;
  }

}
