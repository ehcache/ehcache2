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

import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackages;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.terracotta.test.OsgiUtil.commonOptions;
import static org.terracotta.test.OsgiUtil.getMavenBundle;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.constructs.scheduledrefresh.ScheduledRefreshCacheExtension;
import net.sf.ehcache.constructs.scheduledrefresh.ScheduledRefreshConfiguration;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.terracotta.test.OsgiUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

/**
 * Adapt ScheduledRefreshCacheExtensionTest to run with OSGi.
 * <p>
 * This test duplicates a few test classes in ehcache-scheduled-refresh to simplify bundle loading for osgi deployment
 * </p>
 * 
 * @author cschanck
 * @author hhuynh
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OsgiScheduledRefreshTest {

  private static OddCacheLoader  stupidCacheLoaderOdds  = new OddCacheLoader();
  private static EvenCacheLoader stupidCacheLoaderEvens = new EvenCacheLoader();

  public OsgiScheduledRefreshTest() {
    //
  }

  @Configuration
  public Option[] config() {
    return options(bootDelegationPackages("sun.*,jdk.*"),
        getMavenBundle("net.sf.ehcache", "ehcache-ee", "ehcache"),
        getMavenBundle("org.quartz-scheduler", "quartz"), wrappedBundle(maven("c3p0", "c3p0")
                       .versionAsInProject()),
        commonOptions());
  }

  private static void sleepySeconds(int secs) {
    sleepy(secs * 1000);
  }

  private static void sleepy(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      //
    }
  }

  // OK. we want to create an ehcache, then programmitically decorate it with
  // locks.
  @Test
  public void testIllegalCronExpression() {

    CacheManager manager = new CacheManager();
    manager.removeAllCaches();

    manager.addCache(new Cache(new CacheConfiguration().name("test").eternal(true).maxEntriesLocalHeap(5000)));
    Ehcache cache = manager.getEhcache("test");
    cache.registerCacheLoader(stupidCacheLoaderEvens);
    cache.registerCacheLoader(stupidCacheLoaderOdds);

    ScheduledRefreshConfiguration config = new ScheduledRefreshConfiguration().batchSize(100).quartzThreadCount(4)
        .cronExpression("go to your happy place").build();
    ScheduledRefreshCacheExtension cacheExtension = new ScheduledRefreshCacheExtension(config, cache);
    cache.registerCacheExtension(cacheExtension);
    cacheExtension.init();
    // there will havebeen an exception logged.
    Assert.assertEquals(cacheExtension.getStatus(), Status.STATUS_UNINITIALISED);

    manager.removeAllCaches();
    manager.shutdown();
  }

  @Test
  public void testSimpleCaseProgrammatic() {

    CacheManager manager = new CacheManager();
    manager.removeAllCaches();

    manager.addCache(new Cache(new CacheConfiguration().name("test").eternal(true).maxEntriesLocalHeap(5000)));
    Ehcache cache = manager.getEhcache("test");
    cache.registerCacheLoader(stupidCacheLoaderEvens);
    cache.registerCacheLoader(stupidCacheLoaderOdds);

    int second = (new GregorianCalendar().get(Calendar.SECOND) + 5) % 60;
    ScheduledRefreshConfiguration config = new ScheduledRefreshConfiguration().batchSize(100).quartzThreadCount(4)
        .cronExpression(second + "/5 * * * * ?").build();
    ScheduledRefreshCacheExtension cacheExtension = new ScheduledRefreshCacheExtension(config, cache);
    cache.registerCacheExtension(cacheExtension);
    cacheExtension.init();
    Assert.assertEquals(cacheExtension.getStatus(), Status.STATUS_ALIVE);

    for (int i = 0; i < 10; i++) {
      cache.put(new Element(new Integer(i), i + ""));
    }

    second = Math.max(8, 60 - second + 3);
    System.out.println("Scheduled delay is :: " + second);
    sleepySeconds(second);

    for (Object key : cache.getKeys()) {
      Element val = cache.get(key);
      // System.out.println("["+key+", "+cache.get(key).getObjectValue()+"]");
      int iVal = ((Number) key).intValue();
      if ((iVal & 0x01) == 0) {
        // even
        Assert.assertEquals(iVal + 20000, Long.parseLong((String) val.getObjectValue()));
      } else {
        Assert.assertEquals(iVal + 10000, Long.parseLong((String) val.getObjectValue()));
        // odd
      }

    }

    // cacheExtension.dispose();
    manager.removeAllCaches();
    manager.shutdown();
  }

  // OK. we want to create an ehcache, then programmitaclly decorate it with
  // locks.
  @Test
  public void testSimpleCaseXML() throws Exception {
    net.sf.ehcache.config.Configuration cmConfig = ConfigurationFactory
        .parseConfiguration(OsgiScheduledRefreshTest.class
            .getResource("/net/sf/ehcache/osgi/ehcache-scheduled-refresh.xml"));
    cmConfig.setClassLoader(getClass().getClassLoader());
    CacheManager manager = new CacheManager(cmConfig);

    Cache cache = manager.getCache("sr-test");

    int second = (new GregorianCalendar().get(Calendar.SECOND) + 5) % 60;

    for (int i = 0; i < 10; i++) {
      cache.put(new Element(new Integer(i), i + ""));
    }

    second = Math.max(8, 60 - second + 3);
    System.out.println("Scheduled delay is :: " + second);
    sleepySeconds(second);

    for (Object key : cache.getKeys()) {
      Element val = cache.get(key);
      // System.out.println("["+key+", "+cache.get(key).getObjectValue()+"]");
      int iVal = ((Number) key).intValue();
      if ((iVal & 0x01) == 0) {
        // even
        Assert.assertEquals(iVal + 20000, Long.parseLong((String) val.getObjectValue()));
      } else {
        Assert.assertEquals(iVal + 10000, Long.parseLong((String) val.getObjectValue()));
        // odd
      }

    }
    manager.removeAllCaches();

    manager.shutdown();
  }

  // OK. we want to create an ehcache, then programmitically decorate it with
  // locks.
  @Test
  public void testPolling() {

    CacheManager manager = new CacheManager();
    manager.removeAllCaches();

    manager.addCache(new Cache(new CacheConfiguration().name("tt").eternal(true).maxEntriesLocalHeap(5000)
        .overflowToDisk(false)));
    Ehcache cache = manager.getEhcache("tt");
    stupidCacheLoaderEvens.setMsDelay(100);
    cache.registerCacheLoader(stupidCacheLoaderEvens);
    cache.registerCacheLoader(stupidCacheLoaderOdds);

    int second = (new GregorianCalendar().get(Calendar.SECOND) + 5) % 60;
    ScheduledRefreshConfiguration config = new ScheduledRefreshConfiguration().batchSize(2).quartzThreadCount(2)
        .pollTimeMs(100).cronExpression(second + "/1 * * * * ?").build();
    ScheduledRefreshCacheExtension cacheExtension = new ScheduledRefreshCacheExtension(config, cache);
    cache.registerCacheExtension(cacheExtension);
    cacheExtension.init();
    Assert.assertEquals(cacheExtension.getStatus(), Status.STATUS_ALIVE);

    final int ELEMENT_COUNT = 50;
    long[] orig = new long[ELEMENT_COUNT];
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      Element elem = new Element(new Integer(i), i + "");
      orig[i] = elem.getCreationTime();
      cache.put(elem);
    }

    sleepySeconds(20);

    // cacheExtension.dispose();
    manager.removeAllCaches();
    manager.shutdown();
  }
}
