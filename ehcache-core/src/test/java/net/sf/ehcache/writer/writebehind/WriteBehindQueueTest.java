package net.sf.ehcache.writer.writebehind;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * WriteBehindQueueTest
 */
public class WriteBehindQueueTest {

    /**
     * Test not ideal as it may pass without the fix applied.
     * However, it will not fail with the fix applied.
     */
    @Test
    public void testLastWriteBlockedOnFullQueue() throws InterruptedException {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        CacheWriterConfiguration writerConfiguration = cacheConfiguration.getCacheWriterConfiguration();
        writerConfiguration.setWriteBehindMaxQueueSize(1);
        writerConfiguration.setMinWriteDelay(0);
        final WriteBehindQueue writeBehindQueue = new WriteBehindQueue(cacheConfiguration);

        writeBehindQueue.start(new BlockingWriter());

        writeBehindQueue.write(new Element("a", "a"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeBehindQueue.write(new Element("b", "b"));
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeBehindQueue.write(new Element("c", "c"));
            }
        }).start();

        BlockingWriter.firstLatch.countDown();

        boolean await = BlockingWriter.secondLatch.await(10, TimeUnit.SECONDS);
        if (!await) {
            fail("write still stuck after 10 seconds");
        }
    }

    private static class BlockingWriter implements CacheWriter {

        static CountDownLatch firstLatch = new CountDownLatch(1);
        static CountDownLatch secondLatch = new CountDownLatch(2);

        @Override
        public CacheWriter clone(Ehcache cache) throws CloneNotSupportedException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void init() {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void dispose() throws CacheException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void write(Element element) throws CacheException {
            if (element.getObjectKey().equals("a")) {
                try {
                    firstLatch.await();
                } catch (InterruptedException e) {
                    throw new CacheException(e);
                }
            } else {
                secondLatch.countDown();
            }
        }

        @Override
        public void writeAll(Collection<Element> elements) throws CacheException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void delete(CacheEntry entry) throws CacheException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void deleteAll(Collection<CacheEntry> entries) throws CacheException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public void throwAway(Element element, SingleOperationType operationType, RuntimeException e) {
            throw new UnsupportedOperationException("TODO Implement me!");
        }
    }
}