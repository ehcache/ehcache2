package net.sf.ehcache;

import net.sf.ehcache.event.CacheEventListener;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.greaterThan;

/**
 * @author cschanck
 **/
public class CacheTAB6174Test {

  private class Bar {
    private final String str;

    public Bar(String str) {
      this.str = str;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Bar bar = (Bar) o;

      return !(str != null ? !str.equals(bar.str) : bar.str != null);

    }

    @Override
    public int hashCode() {
      return str != null ? str.hashCode() : 0;
    }
  }

  private static class Foo implements CacheEventListener {
    private AtomicInteger removed=new AtomicInteger(0);
    private AtomicInteger expired=new AtomicInteger(0);
    private AtomicInteger put=new AtomicInteger(0);
    private AtomicInteger updated=new AtomicInteger(0);
    private AtomicInteger evicted=new AtomicInteger(0);

    public int getRemoved() {
      return removed.get();
    }

    public int getExpired() {
      return expired.get();
    }

    public int getPut() {
      return put.get();
    }

    public int getUpdated() {
      return updated.get();
    }

    public int getEvicted() {
      return evicted.get();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    @Override
    public void notifyElementRemoved(Ehcache cache, Element arg1) throws CacheException {
      Object objValue = arg1.getObjectValue();
      if(objValue!=null) {
        removed.incrementAndGet();
        Assert.assertTrue(objValue.getClass().equals(String.class));
      }
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element arg1) throws CacheException {
      put.incrementAndGet();
      Object objValue = arg1.getObjectValue();
      Assert.assertTrue(objValue.getClass().equals(String.class));
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element arg1) throws CacheException {
      updated.incrementAndGet();
      Object objValue = arg1.getObjectValue();
      Assert.assertTrue(objValue.getClass().equals(String.class));
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element arg1) {
      expired.incrementAndGet();
      Object objValue = arg1.getObjectValue();
      Assert.assertTrue(objValue.getClass().equals(String.class));
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element arg1) {
      evicted.incrementAndGet();
      Object objValue = arg1.getObjectValue();
      Assert.assertTrue(objValue.getClass().equals(String.class));
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {

    }

    @Override
    public void dispose() {

    }
  }

  @Test
  public void test6174LegacyLRUMemStore() {
    System.getProperties().put(Cache.NET_SF_EHCACHE_USE_CLASSIC_LRU,"true");
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      "memoryStoreEvictionPolicy=\"LRU\" maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    System.getProperties().remove(Cache.NET_SF_EHCACHE_USE_CLASSIC_LRU);
    actuallyTest(cache);
  }

  @Test
  public void test6174DefaultMemStore() {
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      " maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    actuallyTest(cache);
  }

  @Test
  public void test6174DefaultMemStoreLRU() {
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      " maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "memoryStoreEvictionPolicy=\"LRU\" cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    actuallyTest(cache);
  }

  @Test
  public void test6174DefaultMemStoreLFU() {
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      " maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "memoryStoreEvictionPolicy=\"LFU\" cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    actuallyTest(cache);
  }

  @Test
  public void test6174DefaultMemStoreClock() {
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      " maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "memoryStoreEvictionPolicy=\"CLOCK\" cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    actuallyTest(cache);
  }

  @Test
  public void test6174DefaultMemStoreFIFO() {
    String s = "<ehcache><cache name=\"ServiceResults\" eternal=\"true\" copyOnWrite=\"true\" copyOnRead=\"true\" " +
      " maxEntriesLocalHeap=\"5\" maxEntriesLocalDisk=\"5\" " +
      "memoryStoreEvictionPolicy=\"FIFO\" cacheLoaderTimeoutMillis=\"30000\"> <persistence " +
      "strategy=\"none\"/> </cache></ehcache>";
    CacheManager cm = CacheManager.create(new ByteArrayInputStream(s.getBytes(Charset.forName("utf-8"))));
    Cache cache = cm.getCache("ServiceResults");
    actuallyTest(cache);
  }

  private void actuallyTest(Cache cache) {
    Foo foo = new Foo();
    cache.getCacheEventNotificationService().registerListener(foo);
    for (int i = 0; i < 10; i++) {
      cache.put(new Element(new Bar("foo" + i), "bar" + i, 2, 2));
    }
    for (int i = 0; i < 10; i++) {
      cache.get(new Bar("foo" + i));
    }
    for (int i = 0; i < 7; i++) {
      cache.remove(new Bar("foo" + i));
    }
    cache.put(new Element(new Bar("foo" + 0), "bar2", 2, 2));
    cache.put(new Element(new Bar("foo" + 0), "bar3", 2, 2));
    cache.put(new Element(new Bar("foo100"), "bar100", 1, 1));
    boolean interrupted = false;
    try {
      Thread.sleep(1100);
    } catch (InterruptedException e) {
      interrupted = true;
    }
    cache.get(new Bar("foo100"));
    try {
      Thread.sleep(1100);
    } catch (InterruptedException e) {
      interrupted = true;
    }
    cache.put(new Element(new Bar("foo" + 10), "bar10"));
    Assert.assertThat(foo.getEvicted(), greaterThan(0));
    Assert.assertThat(foo.getPut(), greaterThan(0));
    Assert.assertThat(foo.getRemoved(), greaterThan(0));
    Assert.assertThat(foo.getUpdated(), greaterThan(0));
    Assert.assertThat(foo.getExpired(), greaterThan(0));
    if (interrupted) {
      Thread.currentThread().interrupt();
    }
  }

}
