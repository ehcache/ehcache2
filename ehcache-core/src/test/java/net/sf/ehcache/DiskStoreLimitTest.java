package net.sf.ehcache;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.disk.DiskStoreHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class DiskStoreLimitTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testDiskSizeWhenOversizedDueToPinning() throws Exception {
        long maxBytesLocalDisk = 10000L;
        File storeFolder = tempFolder.newFolder();

        CacheManager cm = CacheManager.create(new Configuration()
                .diskStore(new DiskStoreConfiguration().path(storeFolder.getAbsolutePath())));
        CacheConfiguration config = new CacheConfiguration()
                .name("disk-over-size")
                .maxEntriesLocalHeap(35)
                .maxBytesLocalDisk(maxBytesLocalDisk, MemoryUnit.BYTES)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
        Cache cache = new Cache(config);
        cm.addCache(cache);

        for (int i = 0; i < 200; i++) {
            Element element = new Element(i, "Object" + i);
            cache.put(element);
            DiskStoreHelper.flushAllEntriesToDisk(cache).get();
        }

        int cacheSize = cache.getSize();
        File cacheFile = new File(storeFolder, cache.getName() + ".data");
        long onDiskLength = cacheFile.length();

        DiskStoreHelper.flushAllEntriesToDisk(cache).get();
        System.out.println("Num elements in cache = " + cacheSize);
        System.out.println("Disk store size (bytes):" + cache.getStatistics().getLocalDiskSizeInBytes());
        assertThat(onDiskLength, lessThan((long) (maxBytesLocalDisk * 1.15)));
        cm.shutdown();
    }
}
