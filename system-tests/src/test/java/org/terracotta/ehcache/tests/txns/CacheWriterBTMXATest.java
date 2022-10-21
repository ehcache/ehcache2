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
package org.terracotta.ehcache.tests.txns;

import com.tc.test.config.model.TestConfig;
import org.apache.derby.client.ClientDataSourceFactory;
import org.apache.derby.drda.NetServlet;
import org.apache.derby.iapi.tools.ToolUtils;
import org.apache.derby.impl.db.BasicDatabase;
import org.hibernate.dialect.DerbyDialect;
import org.terracotta.test.util.TestBaseUtil;

import java.util.List;

public class CacheWriterBTMXATest extends AbstractBTMCacheTest {

  public CacheWriterBTMXATest(TestConfig testConfig) {
    super("basic-xa-test.xml", testConfig, CacheWriterBTMTxClient.class);
    testConfig.getClientConfig().setParallelClients(false);
  }

  @Override
  protected List<String> getExtraJars() {
    List<String> extraJars = super.getExtraJars();
    extraJars.add(TestBaseUtil.jarFor(DerbyDialect.class)); // hibernate
    extraJars.add(TestBaseUtil.jarFor(BasicDatabase.class)); // derby
    extraJars.add(TestBaseUtil.jarFor(ToolUtils.class)); // derbytools
    extraJars.add(TestBaseUtil.jarFor(NetServlet.class)); // derbynet
    extraJars.add(TestBaseUtil.jarFor(ClientDataSourceFactory.class)); // derbyclient

    return extraJars;
  }

}
