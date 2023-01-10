/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.ehcache.statistics;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;

import org.junit.Test;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 *
 * @author cdennis
 */
public class PassThroughStatisticsTest {

    @Test
    public void testGetSize() {
        CacheManager manager = new CacheManager(new Configuration().name("foo-manager"));
        try {
            Cache foo = new Cache(new CacheConfiguration().name("foo").maxEntriesLocalHeap(1000));
            manager.addCache(foo);

            ExtendedStatistics extendedStats = foo.getStatistics().getExtended();

            assertThat(extendedStats.size().value().longValue(), is(0L));

            foo.put(new Element("foo", "foo"));

            assertThat(extendedStats.size().value().longValue(), is(1L));
        } finally {
            manager.shutdown();
        }
    }

    @Test
    public void testGetLocalHeapSize() {
        CacheManager manager = new CacheManager(new Configuration().name("foo-manager"));
        try {
            Cache foo = new Cache(new CacheConfiguration().name("foo").maxEntriesLocalHeap(1000));
            manager.addCache(foo);

            ExtendedStatistics extendedStats = foo.getStatistics().getExtended();

            assertThat(extendedStats.localHeapSize().value().longValue(), is(0L));

            foo.put(new Element("foo", "foo"));

            assertThat(extendedStats.localHeapSize().value().longValue(), is(1L));
        } finally {
            manager.shutdown();
        }
    }

    @Test
    public void testGetLocalHeapSizeInBytes() {
        assumeThat(parseInt(getProperty("java.specification.version").split("\\.")[0]), is(lessThan(16)));

        CacheManager manager = new CacheManager(new Configuration().name("foo-manager"));
        try {
            Cache foo = new Cache(new CacheConfiguration().name("foo").maxEntriesLocalHeap(1000));
            manager.addCache(foo);

            ExtendedStatistics extendedStats = foo.getStatistics().getExtended();

            assertThat(extendedStats.localHeapSize().value().longValue(), is(0L));

            foo.put(new Element("foo", "foo"));

            assertThat(extendedStats.localHeapSizeInBytes().value().longValue(), greaterThan(1L));
        } finally {
            manager.shutdown();
        }
    }
}
