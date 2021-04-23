package net.sf.ehcache.management.resource.services;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/cacheManagers/caches/elements endpoint
 * works fine
 *
 * @author Anthony Dahanne
 */
public class ElementsResourceServiceImplTest extends ResourceServiceImplITHelper {
  protected static final String EXPECTED_RESOURCE_LOCATION = "/tc-management-api/agents{agentIds}/cacheManagers{cmIds}/caches{cacheIds}/elements";

  @BeforeClass
  public static void setUpCluster() throws Exception {
    setUpCluster(ElementsResourceServiceImplTest.class);
  }

  @Before
  public void setUp() throws UnsupportedEncodingException {
    cacheManagerMaxBytes = getCacheManagerMaxBytes();
  }

  @Test
  /**
   * - DELETE all elements from a Cache
   *
   * @throws Exception
   */
  public void deleteElementsTest__notSpecifyingCacheOrCacheManager() throws Exception {
    String agentsFilter = "";
    String cmsFilter = "";
    String cachesFilter = "";
    givenStandalone()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(400)
        .body("details", equalTo(""))
        .body("error", equalTo("No cache specified. Unsafe requests must specify a single cache name."))
        .when()
        .delete(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter);

    cachesFilter = ";names=testCache2";
    givenStandalone()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(400)
        .body("details", equalTo(""))
        .body("error", equalTo("No cache manager specified. Unsafe requests must specify a single cache manager name."))
        .when()
        .delete(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter);
  }

  @Test
  /**
   * - DELETE all elements from a Cache
   *
   * @throws Exception
   */
  public void deleteElementsTest() throws Exception {
    Cache exampleCache = cacheManagerMaxBytes.getCache("testCache2");
    for (int i=0; i<1000 ; i++) {
      exampleCache.put(new Element("key" + i, "value" + i));
    }

    givenStandalone()
        .expect()
        .contentType(ContentType.JSON)
        .rootPath("find { it.name == 'testCache2' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.InMemorySize", equalTo(1000))
        .statusCode(200)
        .when()
        .get("/tc-management-api/agents/cacheManagers/caches");

    String agentsFilter = "";
    String cachesFilter = ";names=testCache2";
    String cmsFilter = ";names=testCacheManagerProgrammatic";
    givenStandalone()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(204)
        .when()
        .delete(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter);

    givenStandalone()
        .expect()
        .contentType(ContentType.JSON)
        .rootPath("find { it.name == 'testCache2' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.InMemorySize", equalTo(0))
        .statusCode(200)
        .when()
        .get("/tc-management-api/agents/cacheManagers/caches");
  }

  @Test
  /**
   * - DELETE all elements from a Cache
   *
   * @throws Exception
   */
  public void deleteElementsTest__clustered() throws Exception {
    Cache exampleCache = cacheManagerMaxBytes.getCache("testCache2");
    for (int i=0; i<1000 ; i++) {
      exampleCache.put(new Element("key" + i, "value" + i));
    }
    final String agentsFilter = ";ids=" + cacheManagerMaxBytesAgentId;

    givenClustered()
        .expect()
        .contentType(ContentType.JSON)
        .rootPath("find { it.name == 'testCache2' }")
        .body("agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("attributes.InMemorySize", equalTo(1000))
        .statusCode(200)
        .when()
        .get("/tc-management-api/agents" + agentsFilter + "/cacheManagers/caches");

    String cachesFilter = ";names=testCache2";
    String cmsFilter = ";names=testCacheManagerProgrammatic";
    givenClustered()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(204)
        .when()
        .delete(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter, cachesFilter);

    givenClustered()
        .expect()
        .contentType(ContentType.JSON)
        .rootPath("find { it.name == 'testCache2' }")
        .body("agentId", equalTo(cacheManagerMaxBytesAgentId))
        .body("attributes.InMemorySize", equalTo(0))
        .statusCode(200)
        .when()
        .get("/tc-management-api/agents" + agentsFilter + "/cacheManagers/caches");
  }

  @After
  public void tearDown() {
    if (cacheManagerMaxBytes != null) {
      cacheManagerMaxBytes.shutdown();
    }
  }
}
