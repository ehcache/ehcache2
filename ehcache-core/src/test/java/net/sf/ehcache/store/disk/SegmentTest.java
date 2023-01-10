package net.sf.ehcache.store.disk;

import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.PoolAccessor;

import org.junit.Test;
import org.terracotta.statistics.observer.OperationObserver;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SegmentTest
 */
public class SegmentTest {

    @Test
    public void testInlineEvictionNotified() {
        PoolAccessor onHeapAccessor = mock(PoolAccessor.class);
        when(onHeapAccessor.add(eq("key"), any(DiskStorageFactory.DiskSubstitute.class), any(HashEntry.class), eq(false))).thenReturn(-1L);
        RegisteredEventListeners cacheEventNotificationService = new RegisteredEventListeners(mock(Ehcache.class), null);
        CacheEventListener listener = mock(CacheEventListener.class);
        cacheEventNotificationService.registerListener(listener);

        OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver = mock(OperationObserver.class);
        DiskStorageFactory diskStorageFactory = mock(DiskStorageFactory.class);

        Segment segment = new Segment(10, .95f, diskStorageFactory, mock(CacheConfiguration.class), onHeapAccessor, mock(PoolAccessor.class), cacheEventNotificationService, evictionObserver);
        Element element = new Element("key", "value");
        when(diskStorageFactory.create(element)).thenReturn(new DiskStorageFactory.DiskMarker(diskStorageFactory, 0L, 0, element));
        segment.put("key", 12, element, false, false);
        verify(listener).notifyElementEvicted(any(Ehcache.class), eq(element));
        verify(evictionObserver).end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
    }
}
