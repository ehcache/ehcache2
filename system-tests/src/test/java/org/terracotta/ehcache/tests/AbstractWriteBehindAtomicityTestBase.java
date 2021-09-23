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

import org.terracotta.tests.base.AbstractClientBase;

import com.tc.test.config.model.TestConfig;
import com.tc.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public abstract class AbstractWriteBehindAtomicityTestBase extends AbstractCacheTestBase {

  public AbstractWriteBehindAtomicityTestBase(final String ehcacheConfigPath, TestConfig testConfig,
                                              Class<? extends AbstractClientBase>... c) {
    super(ehcacheConfigPath, testConfig, c);
    testConfig.getClientConfig().getBytemanConfig().setScript("/byteman/writeBehindAtomicity.btm");
    // disableTest();
  }

  // 1) Begin putWithWriter
  // 2) lock() putWithWriter
  // 3) Begin Transaction
  // 4) Commit Transaction
  // 5) unlock() putWithWriter
  // 6) Done putWithWriter
  @Override
  protected void evaluateClientOutput(String clientName, int exitCode, File output) throws Throwable {
    super.evaluateClientOutput(clientName, exitCode, output);
    int txnCount = 0;
    boolean underExplicitLock = false;
    FileReader fr = null;
    BufferedReader reader = null;
    try {
      fr = new FileReader(output);
      reader = new BufferedReader(fr);
      String st = "";
      while ((st = reader.readLine()) != null) {
        // only check for main thread
        if (st.contains("main")) {
          if (st.contains("BEGINOPERATION")) {
            Assert.assertEquals(false, underExplicitLock);
            underExplicitLock = true;
          } else if (st.contains("COMMITTRANSACTION") && underExplicitLock) {
            txnCount++;
            Assert.assertEquals(txnCount, 1);
          } else if (st.contains("ENDOPERATION")) {
            Assert.assertEquals(true, underExplicitLock);
            underExplicitLock = false;
            Assert.assertEquals(txnCount, 1);
            txnCount = 0;
          }
        }
      }
    } catch (Exception e) {
      throw new AssertionError(e);
    } finally {
      try {
        fr.close();
        reader.close();
      } catch (Exception e) {
        //
      }
    }

    Assert.assertEquals(false, underExplicitLock);
    Assert.assertEquals(txnCount, 0);
  }

}