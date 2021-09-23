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
import net.sf.ehcache.management.resource.CacheManagerEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author: Anthony Dahanne
 * The aim of this test is to check via HTTP that the ehcache standalone agent /tc-management-api/agents/cacheManagers/ endpoint
 * works fine
 */
public class CacheManagersResourceServiceImplTest extends ResourceServiceImplITHelper {
  protected static final String EXPECTED_RESOURCE_LOCATION = "/tc-management-api/agents{agentIds}/cacheManagers{cmIds}";

  @BeforeClass
  public static void setUpCluster() throws Exception {
    setUpCluster(CacheManagersResourceServiceImplTest.class);
  }

  @Before
  public void setUp() throws UnsupportedEncodingException {
    cacheManagerMaxBytes = getCacheManagerMaxBytes();
  }

  @Test
  /**
   * - GET the list of cacheManagers
   *
   * @throws Exception
   */
  public void getCacheManagersTest() throws Exception {
    /*
      [
      {
        "version": null,
        "name": "testCacheManagerProgrammatic",
        "agentId": "embedded",
        "attributes": {
          "ClusterUUID": "",
          "Enabled": true,
          "HasWriteBehindWriter": false,
          "MaxBytesLocalDiskAsString": "0",
          "CacheAverageSearchTime": 0,
          "CacheOnDiskHitRate": 0,
          "CachePutRate": 0,
          "CacheMetrics": {
            "testCache2": [
              0,
              0,
              0,
              0
            ]
          },
          "CacheRemoveRate": 0,
          "CacheOffHeapHitRate": 0,
          "Searchable": false,
          "CacheOnDiskMissRate": 0,
          "CacheNames": [
            "testCache2"
          ],
          "TransactionRolledBackCount": 0,
          "CacheInMemoryHitRate": 0,
          "WriterQueueLength": 0,
          "CacheOffHeapMissRate": 0,
          "Transactional": false,
          "CacheHitRate": 0,
          "TransactionCommitRate": 0,
          "CacheExpirationRate": 0,
          "CacheUpdateRate": 0,
          "MaxBytesLocalHeap": 0,
          "CacheAverageGetTime": 0,
          "TransactionRollbackRate": 0,
          "CacheEvictionRate": 0,
          "CacheInMemoryMissRate": 0,
          "MaxBytesLocalDisk": 0,
          "MaxBytesLocalOffHeapAsString": "0",
          "CacheSearchRate": 0,
          "TransactionTimedOutCount": 0,
          "TransactionCommittedCount": 0,
          "Status": "STATUS_ALIVE",
          "MaxBytesLocalOffHeap": 0,
          "WriterMaxQueueSize": 0,
          "MaxBytesLocalHeapAsString": "0",
          "CacheMissRate": 0
        }
      },
      {
        "version": null,
        "name": "testCacheManager",
        "agentId": "embedded",
        "attributes": {
          "ClusterUUID": "",
          "Enabled": true,
          "HasWriteBehindWriter": false,
          "MaxBytesLocalDiskAsString": "0",
          "CacheAverageSearchTime": 0,
          "CacheOnDiskHitRate": 0,
          "CachePutRate": 0,
          "CacheMetrics": {
            "testCache": [
              0,
              0,
              0,
              0
            ]
          },
          "CacheRemoveRate": 0,
          "CacheOffHeapHitRate": 0,
          "Searchable": false,
          "CacheOnDiskMissRate": 0,
          "CacheNames": [
            "testCache"
          ],
          "TransactionRolledBackCount": 0,
          "CacheInMemoryHitRate": 0,
          "WriterQueueLength": 0,
          "CacheOffHeapMissRate": 0,
          "Transactional": false,
          "CacheHitRate": 0,
          "TransactionCommitRate": 0,
          "CacheExpirationRate": 0,
          "CacheUpdateRate": 0,
          "MaxBytesLocalHeap": 0,
          "CacheAverageGetTime": 0,
          "TransactionRollbackRate": 0,
          "CacheEvictionRate": 0,
          "CacheInMemoryMissRate": 0,
          "MaxBytesLocalDisk": 0,
          "MaxBytesLocalOffHeapAsString": "0",
          "CacheSearchRate": 0,
          "TransactionTimedOutCount": 0,
          "TransactionCommittedCount": 0,
          "Status": "STATUS_ALIVE",
          "MaxBytesLocalOffHeap": 0,
          "WriterMaxQueueSize": 0,
          "MaxBytesLocalHeapAsString": "0",
          "CacheMissRate": 0
        }
      }
    ]
     */
    String agentsFilter = "";
    String cmsFilter = "";
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .body("size()", is(2))
      .rootPath("find { it.name == 'testCacheManagerProgrammatic' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.CacheMetrics.testCache2", hasItems(0, 0, 0, 0))
        .body("attributes.CacheNames.get(0)", equalTo("testCache2"))
      .rootPath("find { it.name == 'testCacheManager' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.CacheMetrics.testCache", hasItems(0, 0, 0, 0))
        .body("attributes.CacheNames.get(0)", equalTo("testCache"))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    cmsFilter = ";names=testCacheManagerProgrammatic";
    // we filter to return only the attribute CacheNames, and working only on the testCacheManagerProgrammatic CM
    givenStandalone()
      .queryParam("show", "CacheMetrics")
      .queryParam("show", "CacheNames")
    .expect()
      .contentType(ContentType.JSON)
      .body("get(0).agentId", equalTo("embedded"))
      .body("get(0).name", equalTo("testCacheManagerProgrammatic"))
      .body("get(0).attributes.CacheMetrics.testCache2", hasItems(0, 0, 0, 0))
      .body("get(0).attributes.CacheNames.get(0)", equalTo("testCache2"))
      .body("size()",is(1))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @Test
  /**
   * - PUT an updated CacheManagerEntity
   *
   * @throws Exception
   */
  public void updateCacheManagersTest__FailWhenNotSpecifyingACacheManager() throws Exception {
    // you have to specify a cacheManager when doing mutation
    CacheManagerEntity cacheManagerEntity = new CacheManagerEntity();
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("Searchable",Boolean.TRUE);
    attributes.put("Enabled", Boolean.FALSE);
    cacheManagerEntity.getAttributes().putAll(attributes);

    String agentsFilter = "";
    String cmsFilter = "";

    givenStandalone()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .statusCode(400)
      .body("details", equalTo(""))
      .body("error", equalTo("No cache manager specified. Unsafe requests must specify a single cache manager name."))
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    cmsFilter = ";names=pif";
    givenStandalone()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .statusCode(400)
      .body("details", equalTo("CacheManager not found !"))
      .body("error", equalTo("Failed to update cache manager"))
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    cmsFilter = "";
    // we check nothing has changed
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .rootPath("find { it.name == 'testCacheManagerProgrammatic' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.CacheMetrics.testCache2", hasItems(0, 0, 0, 0))
        .body("attributes.CacheNames.get(0)", equalTo("testCache2"))
      .rootPath("find { it.name == 'testCacheManager' }")
        .body("agentId", equalTo("embedded"))
        .body("attributes.CacheMetrics.testCache", hasItems(0, 0, 0, 0))
        .body("attributes.CacheNames.get(0)", equalTo("testCache"))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @Test
  /**
   * - PUT an updated CacheManagerEntity
   * only 2 attributes are supported
   * @throws Exception
   */
  public void updateCacheManagersTest() throws Exception {
    // you have to specify a cacheManager when doing mutation
    CacheManagerEntity cacheManagerEntity = new CacheManagerEntity();
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("MaxBytesLocalHeapAsString","20M");
    attributes.put("MaxBytesLocalDiskAsString", "40M");
    cacheManagerEntity.getAttributes().putAll(attributes);

    String agentsFilter = "";
    String cmsFilter = ";names=testCacheManagerProgrammatic";

    givenStandalone()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .log().ifStatusCodeIsEqualTo(204)
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .body("get(0).agentId", equalTo("embedded"))
      .body("get(0).name", equalTo("testCacheManagerProgrammatic"))
      .body("get(0).attributes.MaxBytesLocalHeapAsString", equalTo("20M"))
      .body("get(0).attributes.MaxBytesLocalDiskAsString", equalTo("40M"))
      .body("size()",is(1))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @Test
  /**
   * - PUT an updated CacheManagerEntity
   * only 2 attributes are supported
   * @throws Exception
   */
  public void updateCacheManagersTest__clustered() throws Exception {
    // you have to specify a cacheManager when doing mutation
    CacheManagerEntity cacheManagerEntity = new CacheManagerEntity();
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("MaxBytesLocalHeapAsString","12M");
    attributes.put("MaxBytesLocalDiskAsString", "6M");
    cacheManagerEntity.getAttributes().putAll(attributes);

    final String agentsFilter = ";ids=" + cacheManagerMaxBytesAgentId;
    String cmsFilter = ";names=testCacheManagerProgrammatic";

    givenClustered()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .log().ifStatusCodeIsEqualTo(204)
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    givenClustered()
      .contentType(ContentType.JSON)
    .expect()
      .contentType(ContentType.JSON)
      .body("get(0).agentId", equalTo(cacheManagerMaxBytesAgentId))
      .body("get(0).name", equalTo("testCacheManagerProgrammatic"))
      .body("get(0).attributes.MaxBytesLocalHeapAsString", equalTo("12M"))
      .body("get(0).attributes.MaxBytesLocalDiskAsString", equalTo("6M"))
      .body("size()",is(1))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @Test
  /**
   * - PUT an updated CacheManagerEntity, with attributes not allowed
   * only 2 attributes are supported, the others are forbidden because we do not allow them to be updated
   * @throws Exception
   */
  public void updateCacheManagersTest__FailWhenMutatingForbiddenAttributes() throws Exception {
    // you have to specify a cacheManager when doing mutation
    CacheManagerEntity cacheManagerEntity = new CacheManagerEntity();
    cacheManagerEntity.setName("superName");
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("MaxBytesLocalHeap", "20000");
    attributes.put("MaxBytesLocalDisk", "40000");
    cacheManagerEntity.getAttributes().putAll(attributes);

    String agentsFilter = "";
    String cmsFilter = ";names=testCacheManagerProgrammatic";

    givenStandalone()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .log().ifStatusCodeIsEqualTo(400)
      .body("details", allOf(containsString("You are not allowed to update those attributes : name "),
                             containsString("MaxBytesLocalDisk"), containsString("MaxBytesLocalHeap"),
                             containsString(" . Only MaxBytesLocalDiskAsString and MaxBytesLocalHeapAsString can be updated for a CacheManager.")))
      .body("error", equalTo("Failed to update cache manager"))
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);

    // we check nothing has changed
    givenStandalone()
    .expect()
      .contentType(ContentType.JSON)
      .body("get(0).agentId", equalTo("embedded"))
      .body("get(0).name", equalTo("testCacheManagerProgrammatic"))
      .body("get(0).attributes.MaxBytesLocalHeapAsString", equalTo("5M"))
      .body("get(0).attributes.MaxBytesLocalDiskAsString", equalTo("10M"))
      .body("size()",is(1))
      .statusCode(200)
    .when()
      .get(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @Test
  /**
   * - PUT an updated CacheManagerEntity
   * @throws Exception
   */
  public void updateCacheManagersTest__CacheManagerDoesNotExist() throws Exception {
    // you have to specify a cacheManager when doing mutation
    CacheManagerEntity cacheManagerEntity = new CacheManagerEntity();

    String agentsFilter = "";
    String cmsFilter = ";names=CacheManagerDoesNotExist";
    givenStandalone()
      .contentType(ContentType.JSON)
      .body(cacheManagerEntity)
    .expect()
      .log().ifStatusCodeIsEqualTo(404)
      .statusCode(400)
      .body("details", equalTo("CacheManager not found !"))
      .body("error", equalTo("Failed to update cache manager"))
    .when()
      .put(EXPECTED_RESOURCE_LOCATION, agentsFilter, cmsFilter);
  }

  @After
  public void tearDown() {
    if (cacheManagerMaxBytes != null) {
      cacheManagerMaxBytes.shutdown();
    }
  }
}
