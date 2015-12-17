package net.sf.ehcache.config.generator;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.MemoryUnit;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * ConfigurationUtilTest
 */
public class ConfigurationUtilTest {

    @Test
    public void testCloningRespectsOverflowToOffHeapSetToFalse() throws Exception {
        Configuration configuration = new Configuration();
        configuration.maxBytesLocalOffHeap(12, MemoryUnit.MEGABYTES);
        configuration.defaultCache(new CacheConfiguration().maxEntriesLocalHeap(10).overflowToOffHeap(false));

        String configText = ConfigurationUtil.generateCacheManagerConfigurationText(configuration);

        assertThat(configuration.getDefaultCacheConfiguration().isOverflowToOffHeapSet(), is(true));
        assertThat(configText, containsString("overflowToOffHeap=\"false\""));
    }

    @Test
    public void testCloningUnsetOverflowToOffHeap() {
        Configuration configuration = new Configuration();
        configuration.maxBytesLocalOffHeap(12, MemoryUnit.MEGABYTES);
        configuration.defaultCache(new CacheConfiguration().maxEntriesLocalHeap(10));

        String configText = ConfigurationUtil.generateCacheManagerConfigurationText(configuration);

        assertThat(configuration.getDefaultCacheConfiguration().isOverflowToOffHeapSet(), is(false));
        assertThat(configText, not(containsString("overflowToOffHeap=\"false\"")));
    }
}

