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
package org.terracotta.ehcache.tests.loader;

import net.sf.ehcache.CacheManager;

import org.objectweb.asm.ClassWriter;
import org.terracotta.ehcache.tests.AbstractCacheTestBase;
import org.terracotta.test.util.TestBaseUtil;
import org.terracotta.toolkit.Toolkit;

import com.tc.test.config.model.TestConfig;

import java.io.File;
import java.util.Arrays;

public class LoaderTest extends AbstractCacheTestBase {

  private static final String EHCACHE_XML = "small-memory-cache-test.xml";

  public LoaderTest(TestConfig testConfig) {
    super(EHCACHE_XML, testConfig);
  }

  @Override
  protected String createClassPath(Class client) {
    return "";
  }

  @Override
  protected void startClients() throws Throwable {

    StringBuilder sb = new StringBuilder();

    sb.append(writeEhcacheConfigWithPort(EHCACHE_XML)).append(File.pathSeparator);
    sb.append(writeXmlFileWithPort("logback.xml", "logback.xml")).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(LoaderClient.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(CacheManager.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(org.slf4j.LoggerFactory.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(org.slf4j.impl.StaticLoggerBinder.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(ch.qos.logback.classic.LoggerContext.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(ch.qos.logback.core.Layout.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(org.apache.commons.logging.LogFactory.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(ClassWriter.class)).append(File.pathSeparator); // needed for OtherClassloaderClient
    sb.append(TestBaseUtil.jarFor(org.junit.Assert.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(javax.transaction.xa.XAException.class)).append(File.pathSeparator);
    sb.append(TestBaseUtil.jarFor(Toolkit.class));

    runClient(LoaderClient.class, LoaderClient.class.getSimpleName(), Arrays.asList(sb.toString()));
  }
}
