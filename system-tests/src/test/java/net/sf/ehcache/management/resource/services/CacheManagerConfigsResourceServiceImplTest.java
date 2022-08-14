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

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/cacheManagers/config endpoint
 * works fine
 * @author Anthony Dahanne
 */
public class CacheManagerConfigsResourceServiceImplTest extends ResourceServiceImplITHelper {
  protected static final String EXPECTED_RESOURCE_LOCATION = "/tc-management-api/agents{agentIds}/cacheManagers{cmIds}/configs";

  @BeforeClass
  public static void setUpCluster() throws Exception {
    setUpCluster(CacheManagerConfigsResourceServiceImplTest.class);
  }

  @Before
  public void setUp() throws UnsupportedEncodingException {
    cacheManagerMaxBytes = getCacheManagerMaxBytes();
  }

  @Test
  /**
   * - GET the list of cacheManagers configs
   *
   * @throws Exception
   */
  public void getCacheManagersTest() throws Exception {
    String agentsFilter = "";
    String cmsFilter = "";

    String xml = givenStandalone()
      .expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.agentId", equalTo("embedded"))
        .body("find { it.cacheManagerName == 'testCacheManager' }.agentId", equalTo("embedded"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.xml").toString();

    XmlPath xmlPath = new XmlPath(xml);
    Node cacheManager = xmlPath.getNode("ehcache");
    assertEquals("testCacheManagerProgrammatic", cacheManager.attributes().get("name"));
    assertEquals("5M", cacheManager.attributes().get("maxBytesLocalHeap"));
    assertEquals("10M", cacheManager.attributes().get("maxBytesLocalDisk"));
    Node cache = cacheManager.getNode("cache");
    assertEquals("testCache2", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    Node managementRESTService =  cacheManager.getNode("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    Node terracottaConfig = cacheManager.getNode("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));

    //same thing but we specify only a given cacheManager
    agentsFilter = "";
    cmsFilter = ";names=testCacheManager";

    String filteredXml = givenStandalone()
      .expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo("embedded"))
        .body("[0].cacheManagerName", equalTo("testCacheManager"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);

    cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManager", cacheManager.attributes().get("name"));
    cache = cacheManager.getNode("cache");
    assertEquals("testCache", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    managementRESTService = cacheManager.getNode("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    terracottaConfig = cacheManager.getNode("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));
  }

  @Test
  public void getCacheManagersTest__clustered() throws Exception {
    String agentsFilter = ";ids=" + cacheManagerMaxBytesAgentId + "," + cacheManagerMaxElementsAgentId;
    String cmsFilter = "";

    System.out.println( "response: \n" +
      givenClustered().get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter).asString()
    );

    String xml = givenClustered()
      .expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("find { it.cacheManagerName == 'testCacheManager' }.agentId", equalTo(cacheManagerMaxElementsAgentId))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.xml").toString();

    XmlPath xmlPath = new XmlPath(xml);
    Node cacheManager = xmlPath.getNode("ehcache");
    assertEquals("testCacheManagerProgrammatic", cacheManager.attributes().get("name"));
    assertEquals("5M", cacheManager.attributes().get("maxBytesLocalHeap"));
    assertEquals("10M", cacheManager.attributes().get("maxBytesLocalDisk"));
    Node cache = cacheManager.getNode("cache");
    assertEquals("testCache2", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    Node managementRESTService = cacheManager.getNode("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    Node terracottaConfig = cacheManager.getNode("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));

    //same thing but we specify only a given cacheManager
    agentsFilter = "";
    cmsFilter = ";names=testCacheManager";

    String filteredXml = givenClustered()
      .expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo(cacheManagerMaxElementsAgentId))
        .body("[0].cacheManagerName", equalTo("testCacheManager"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);

    cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManager", cacheManager.attributes().get("name"));
    cache = cacheManager.getNode("cache");
    assertEquals("testCache", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    managementRESTService = cacheManager.getNode("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    terracottaConfig = cacheManager.getNode("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));
  }

  @After
  public void tearDown() {
    if (cacheManagerMaxBytes != null) {
      cacheManagerMaxBytes.shutdown();
    }
  }
}
