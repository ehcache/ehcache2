package net.sf.ehcache.management.resource.services;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;

/**
 * @author: Anthony Dahanne
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/ endpoint
 * works fine
 */
public class AgentsResourceServiceImplTest extends ResourceServiceImplITHelper {
  protected static final String EXPECTED_RESOURCE_LOCATION = "/tc-management-api/agents";

  @BeforeClass
  public static void setUpCluster() throws Exception {
    setUpCluster(AgentsResourceServiceImplTest.class);
  }

  @Test
  /**
   * - GET the list of agents
   * - GET the subresource /info
   *
   * @throws Exception
   */
  public void getAgentsTest__OneCacheManager() throws Exception {
    // [{"version":null,"agentId":"embedded","agencyOf":"Ehcache","rootRepresentables":{"cacheManagerNames":"testCacheManager"}}]
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .rootPath("get(0)")
      .body("agentId", equalTo("embedded"))
      .body("agencyOf", equalTo("Ehcache"))
      .body("rootRepresentables.cacheManagerNames", equalTo("testCacheManager"))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION);

    // [{"version":null,"agentId":"embedded","agencyOf":"Ehcache","rootRepresentables":{"cacheManagerNames":"testCacheManager"}}]
    givenStandalone()
    .expect().contentType(ContentType.JSON)
      .rootPath("get(0)")
      .body("agentId", equalTo("embedded"))
      .body("agencyOf", equalTo("Ehcache"))
      .body("rootRepresentables.cacheManagerNames", equalTo("testCacheManager"))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION +";ids=embedded");

    // [{"version":null,"agentId":"embedded","agencyOf":"Ehcache","rootRepresentables":{"cacheManagerNames":"testCacheManager"}}]
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .statusCode(400)
      .when().get(EXPECTED_RESOURCE_LOCATION +";ids=w00t");

    // /info
    //[{"agentId":"embedded","agencyOf":"Ehcache","available":true,"secured":false,"sslEnabled":false,"needClientAuth":false,"licensed":false,"sampleHistorySize":30,"sampleIntervalSeconds":1,"enabled":true,"restAPIVersion":null}]
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .rootPath("get(0)")
      .body("agentId", equalTo("embedded"))
      .body("agencyOf", equalTo("Ehcache"))
      .body("available", equalTo(true))
      .body("secured", equalTo(false))
      .body("sslEnabled", equalTo(false))
      .body("needClientAuth", equalTo(false))
      .body("licensed", equalTo(false))
      .body("sampleHistorySize", equalTo(30))
      .body("sampleIntervalSeconds", equalTo(1))
      .body("enabled", equalTo(true))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION + INFO);

    // /info
    //[{"agentId":"embedded","agencyOf":"Ehcache","available":true,"secured":false,"sslEnabled":false,"needClientAuth":false,"licensed":false,"sampleHistorySize":30,"sampleIntervalSeconds":1,"enabled":true,"restAPIVersion":null}]
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .rootPath("get(0)")
      .body("agentId", equalTo("embedded"))
      .body("agencyOf", equalTo("Ehcache"))
      .body("available", equalTo(true))
      .body("secured", equalTo(false))
      .body("sslEnabled", equalTo(false))
      .body("needClientAuth", equalTo(false))
      .body("licensed", equalTo(false))
      .body("sampleHistorySize", equalTo(30))
      .body("sampleIntervalSeconds", equalTo(1))
      .body("enabled", equalTo(true))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION  +";ids=embedded"+ INFO);
  }

  @Test
  /**
   * - GET the list of agents
   *
   * @throws Exception
   */
  public void getAgentsTest__TwoCacheManagers() throws Exception {
    // we configure the second cache manager programmatically
    cacheManagerMaxBytes = getCacheManagerMaxBytes();
    // let's check the agent was edited correctly server side
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .rootPath("get(0)")
      .body("agentId", equalTo("embedded"))
      .body("agencyOf", equalTo("Ehcache"))
      .body("rootRepresentables.cacheManagerNames", allOf(containsString("testCacheManagerProgrammatic"), containsString("testCacheManager")))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION);
    cacheManagerMaxBytes.clearAll();
    cacheManagerMaxBytes.shutdown();
  }

  @Test
  public void getAgentsTest__clustered() throws Exception {
    // [{"version":null,"agentId":"embedded","agencyOf":"Ehcache","rootRepresentables":{"cacheManagerNames":"testCacheManager"}}]
    givenClustered()
    .expect()
      .contentType(ContentType.JSON)
      .body("get(0).agentId", Matchers.equalTo("embedded"))
      .body("get(0).agencyOf", Matchers.equalTo("TSA"))
      .body("get(0).rootRepresentables.urls", Matchers.equalTo("http://localhost:" + MANAGEMENT_PORT))
      .body("get(1).agentId", anyOf(containsString("localhost_"), containsString("127.0.0.1_"), containsString("localhost.localdomain_"), containsString("localhost.home_")))
      .body("get(1).agencyOf", Matchers.equalTo("Ehcache"))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION);
  }
}
