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
package net.sf.ehcache.management.resource.services;

import io.restassured.path.xml.element.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;

import java.io.UnsupportedEncodingException;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/cacheManagers/caches/config endpoint
 * works fine
 * @author Anthony Dahanne
 */
public class CacheConfigsResourceServiceImplTest extends ResourceServiceImplITHelper {
  protected static final String EXPECTED_RESOURCE_LOCATION = "/tc-management-api/agents{agentIds}/cacheManagers{cmIds}/caches{cacheIds}/configs";

  @BeforeClass
  public static void setUpCluster() throws Exception {
    setUpCluster(CacheConfigsResourceServiceImplTest.class);
  }

  @Before
  public void setUp() throws UnsupportedEncodingException {
    cacheManagerMaxBytes = getCacheManagerMaxBytes();
  }

  @Test
  /**
   * - GET the list of caches configs
   *
   * @throws Exception
   */
  public void getCacheConfigsTest() throws Exception {
    String agentsFilter = "";
    String cmsFilter = "";
    String cachesFilter = "";

    String xml =
      givenStandalone()
      .expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.cacheName", is("testCache2"))
        .body("find { it.cacheManagerName == 'testCacheManager' }.cacheName", is("testCache"))
        .body("[0].agentId", equalTo("embedded"))
        .body("[1].agentId", equalTo("embedded"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManager' }.xml").toString();

    XmlPath xmlPath = new XmlPath(xml);
    Node cache = xmlPath.getNode("cache");
    assertEquals("testCache", cache.attributes().get("name"));

    //same thing but we specify only a given cacheManager
    agentsFilter = "";
    cmsFilter = ";names=testCacheManagerProgrammatic";
    cachesFilter = ";names=testCache2";

    String filteredXml = givenStandalone()
      .expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo("embedded"))
        .body("[0].cacheManagerName", equalTo("testCacheManagerProgrammatic"))
        .body("[0].cacheName", equalTo("testCache2"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);
    cache = xmlPath.get("cache");
    assertEquals("testCache2", cache.attributes().get("name"));
  }

  @Test
  public void getCacheConfigsTest__clustered() throws Exception {
    String cmsFilter = "";
    String cachesFilter = "";
      String agentsFilter = ";ids=" + cacheManagerMaxBytesAgentId + "," + cacheManagerMaxElementsAgentId;

    String xml = givenClustered()
      .expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.cacheName", is("testCache2"))
        .body("find { it.cacheManagerName == 'testCacheManager' }.cacheName", is("testCache"))
        .body("find { it.cacheManagerName == 'testCacheManager' }.agentId", equalTo(cacheManagerMaxElementsAgentId))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManager' }.xml").toString();

    XmlPath xmlPath = new XmlPath(xml);
    Node cache = xmlPath.getNode("cache");
    assertEquals("testCache", cache.attributes().get("name"));

    //same thing but we specify only a given cacheManager
    cmsFilter = ";names=testCacheManagerProgrammatic";
    cachesFilter = ";names=testCache2";

    String filteredXml = givenClustered()
      .expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("[0].cacheManagerName", equalTo("testCacheManagerProgrammatic"))
        .body("[0].cacheName", equalTo("testCache2"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);
    cache = xmlPath.get("cache");
    assertEquals("testCache2", cache.attributes().get("name"));
  }

  @After
  public void tearDown() {
    if (cacheManagerMaxBytes != null) {
      cacheManagerMaxBytes.shutdown();
    }
  }
}
