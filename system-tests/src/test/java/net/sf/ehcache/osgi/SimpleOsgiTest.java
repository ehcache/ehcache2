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
package net.sf.ehcache.osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackages;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.terracotta.test.OsgiUtil.commonOptions;
import static org.terracotta.test.OsgiUtil.getMavenBundle;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.terracotta.context.ContextManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;

/**
 * Test a simple BigMemory usage with BM Go license key. The product name should include "BigMemory" and not "Ehcache"
 * NOTE: this test only works in fullmode
 * 
 * @author hhuynh
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SimpleOsgiTest {

  @Configuration
  public Option[] config() {
    return options(bootDelegationPackages("sun.*,jdk.*"),
        // need this for REST agent test
        getMavenBundle("net.sf.ehcache", "ehcache-ee", "ehcache"),
        commonOptions());
  }

  @Test
  public void testSimpleCache() throws Exception {
    CacheManager manager = new CacheManager(SimpleOsgiTest.class.getResource("/net/sf/ehcache/osgi/simple-ehcache.xml"));
    try {
      Cache cache = manager.getCache("sampleCache1");
      Element element = new Element("key1", "value1");
      cache.put(element);
      Element element1 = cache.get("key1");
      assertEquals("value1", element1.getObjectValue());
      assertEquals(1, cache.getSize());
    } finally {
      manager.shutdown();
    }
  }

  @Test
  public void testValueClass() throws Exception {
    CacheManager manager = new CacheManager(SimpleOsgiTest.class.getResource("/net/sf/ehcache/osgi/simple-ehcache.xml"));
    try {
      Cache cache = manager.getCache("sampleCache1");
      Element element = new Element("key1", new Value("value1"));
      cache.put(element);
      Element element1 = cache.get("key1");
      assertEquals("value1", ((Value) element1.getObjectValue()).v);
      assertEquals(1, cache.getSize());
    } finally {
      manager.shutdown();
    }
  }

  @Test
  public void testUsingNonExportedClass() {
    try {
      ContextManager cm = new ContextManager();
      fail("Expected class not found exception");
    } catch (Throwable e) {
      // expected
    }
  }

  @Test
  public void testRestAgent() throws Exception {
    CacheManager manager = new CacheManager(
                                            SimpleOsgiTest.class
                                                .getResource("/net/sf/ehcache/osgi/rest-enabled-ehcache.xml"));
    InputStream in = null;
    try {
      Cache testCache = manager.getCache("testCache");
      testCache.put(new Element("k", "v"));
      assertEquals(1, testCache.getSize());
      URL url = new URL("http://localhost:9888/tc-management-api/agents");
      in = url.openStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line = null;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line.trim()).append("\n");
      }
      System.out.println("Rest response: " + sb);
      assertTrue(sb.toString().contains("\"agentId\":\"embedded\""));
    } finally {
      manager.shutdown();
      if (in != null) {
        in.close();
      }
    }
  }

  private static class Value implements Serializable {
    public String v;

    public Value(String value) {
      v = value;
    }
  }
}