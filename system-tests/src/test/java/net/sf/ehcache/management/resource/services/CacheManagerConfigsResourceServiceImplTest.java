package net.sf.ehcache.management.resource.services;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.path.xml.NodeImpl;
import com.jayway.restassured.path.xml.XmlPath;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/cacheManagers/config endpoint
 * works fine
 * @author Anthony Dahanne
 */
public class CacheManagerConfigsResourceServiceImplTest extends ResourceServiceImplITHelper {

  protected static final String EXPECTED_RESOURCE_LOCATION = "{baseUrl}/tc-management-api/agents{agentIds}/cacheManagers{cmIds}/configs";

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

    String xml = expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.agentId", equalTo("embedded"))
        .body("find { it.cacheManagerName == 'testCacheManager' }.agentId", equalTo("embedded"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, STANDALONE_BASE_URL, agentsFilter, cmsFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.xml").toString();

    XmlPath xmlPath = new XmlPath(xml);
    NodeImpl cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManagerProgrammatic", cacheManager.attributes().get("name"));
    assertEquals("5M", cacheManager.attributes().get("maxBytesLocalHeap"));
    assertEquals("10M", cacheManager.attributes().get("maxBytesLocalDisk"));
    NodeImpl cache = cacheManager.get("cache");
    assertEquals("testCache2", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    NodeImpl managementRESTService = cacheManager.get("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    NodeImpl terracottaConfig = cacheManager.get("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));


    //same thing but we specify only a given cacheManager
    agentsFilter = "";
    cmsFilter = ";names=testCacheManager";

    String filteredXml = expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo("embedded"))
        .body("[0].cacheManagerName", equalTo("testCacheManager"))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, STANDALONE_BASE_URL, agentsFilter, cmsFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);

    cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManager", cacheManager.attributes().get("name"));
    cache = cacheManager.get("cache");
    assertEquals("testCache", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    managementRESTService = cacheManager.get("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    terracottaConfig = cacheManager.get("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));
  }

  @Test
  public void getCacheManagersTest__clustered() throws Exception {
    String agentsFilter = ";ids=" + cacheManagerMaxBytesAgentId + "," + cacheManagerMaxElementsAgentId;
    String cmsFilter = "";

    System.out.println( "response: \n" +
      get(EXPECTED_RESOURCE_LOCATION, CLUSTERED_BASE_URL, agentsFilter, cmsFilter).asString()
    );

    String xml = expect()
        .contentType(ContentType.JSON)
        .body("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("find { it.cacheManagerName == 'testCacheManager' }.agentId", equalTo(cacheManagerMaxElementsAgentId))
        .statusCode(200)
      .when()
        .get(EXPECTED_RESOURCE_LOCATION, CLUSTERED_BASE_URL, agentsFilter, cmsFilter)
        .jsonPath().get("find { it.cacheManagerName == 'testCacheManagerProgrammatic' }.xml").toString();


    XmlPath xmlPath = new XmlPath(xml);
    NodeImpl cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManagerProgrammatic", cacheManager.attributes().get("name"));
    assertEquals("5M", cacheManager.attributes().get("maxBytesLocalHeap"));
    assertEquals("10M", cacheManager.attributes().get("maxBytesLocalDisk"));
    NodeImpl cache = cacheManager.get("cache");
    assertEquals("testCache2", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    NodeImpl managementRESTService = cacheManager.get("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    NodeImpl terracottaConfig = cacheManager.get("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));


    //same thing but we specify only a given cacheManager
    agentsFilter = "";
    cmsFilter = ";names=testCacheManager";

    String filteredXml = expect()
        .contentType(ContentType.JSON)
        .body("[0].agentId", equalTo(cacheManagerMaxElementsAgentId))
        .body("[0].cacheManagerName", equalTo("testCacheManager"))
        .statusCode(200)
        .when()
        .get(EXPECTED_RESOURCE_LOCATION, CLUSTERED_BASE_URL, agentsFilter, cmsFilter)
        .jsonPath().get("[0].xml").toString();

    xmlPath = new XmlPath(filteredXml);

    cacheManager = xmlPath.get("ehcache");
    assertEquals("testCacheManager", cacheManager.attributes().get("name"));
    cache = cacheManager.get("cache");
    assertEquals("testCache", cache.getAttribute("name"));
    assertNotNull(cache.get("terracotta"));
    managementRESTService = cacheManager.get("managementRESTService");
    assertEquals("true", managementRESTService.getAttribute("enabled"));
    assertEquals("0.0.0.0:" + STANDALONE_REST_AGENT_PORT, managementRESTService.getAttribute("bind"));
    terracottaConfig = cacheManager.get("terracottaConfig");
    assertNotNull(terracottaConfig.getAttribute("url"));
  }


  @After
  public void tearDown() {
    if (cacheManagerMaxBytes != null) {
      cacheManagerMaxBytes.shutdown();
    }
  }

}
