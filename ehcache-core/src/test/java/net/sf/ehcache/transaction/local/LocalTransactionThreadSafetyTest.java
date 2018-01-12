package net.sf.ehcache.transaction.local;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.ehcache.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.config.CacheConfiguration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LocalTransactionThreadSafetyTest {

  private ExecutorService executorService;
  private TxCache txCache;

  @Before
  public void setUp() {
    executorService = Executors.newFixedThreadPool(2);
    txCache = TxCacheBuilder.build();
  }

  @After
  public void tearDown() {
    executorService.shutdown();
    txCache.close();
  }

  @Test
  public void shouldNotModifyEntryConcurrently() throws Exception {
    // test boolean replace(Element old, Element element, ElementValueComparator comparator)
    for (int i = 0; i < 100; ++i) {
      runConcurrentlyTwoTasksAddingOne();
    }
  }

  @Test
  public void shouldNotRemoveModifiedEntry() throws Exception {
    // test Element removeElement(Element element, ElementValueComparator comparator)
    for (int i = 0; i < 100; ++i) {
      runConcurrentlyTwoTasksRemovingElement();
    }
  }

  private void runConcurrentlyTwoTasksAddingOne() throws Exception {
    txCache.put("MyKey", new CacheValue(0));

    final List<Future<Boolean>> results = executorService.invokeAll(Arrays.asList(
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return txCache.increaseByOne("MyKey");
          }
        },
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return txCache.increaseByOne("MyKey");
          }
        }
    ));

    boolean allReplaceCallsSucceeded = true;
    for (Future<Boolean> result : results) {
      allReplaceCallsSucceeded &= result.get();
    }

    assertThat(txCache.get("MyKey").getCurrentNumber() == 1 && allReplaceCallsSucceeded, is(false));
  }

  private void runConcurrentlyTwoTasksRemovingElement() throws Exception {
    txCache.put("MyKey", new CacheValue(2));

    final List<Future<Boolean>> results = executorService.invokeAll(Arrays.asList(
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return txCache.increaseByOne("MyKey");
          }
        },
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return txCache.removeIfPresent("MyKey", 2);
          }
        }
    ));

    boolean allCallsSucceeded = true;
    boolean atLeastOneCallSucceeded = false;
    for (Future<Boolean> result : results) {
      boolean success = result.get();
      if (success) {
        atLeastOneCallSucceeded = true;
      }
      allCallsSucceeded &= success;
    }

    CacheValue value = txCache.get("MyKey");
    if (value != null) {
      assertThat(value, is(new CacheValue(3)));
    }
    assertThat(atLeastOneCallSucceeded, is(true));
    assertThat(allCallsSucceeded, is(false));
  }


  static class CacheValue {
    private final int currentNumber;

    private CacheValue(final int currentNumber) {
      this.currentNumber = currentNumber;
    }

    static CacheValue increaseByOne(final CacheValue value) {
      return new CacheValue(value.currentNumber + 1);
    }

    int getCurrentNumber() {
      return currentNumber;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CacheValue that = (CacheValue) o;
      return currentNumber == that.currentNumber;
    }

    @Override
    public int hashCode() {
      return currentNumber;
    }
  }

  static class TxCache {
    private final CacheManager cacheManager;
    private final Cache cache;

    TxCache(final CacheManager cacheManager, final Cache cache) {
      this.cacheManager = cacheManager;
      this.cache = cache;
    }

    void put(final String key, final CacheValue value) {
      try {
        txRequired(new Callable<Void>() {
          @Override
          public Void call() throws Exception {
            cache.put(new Element(key, value));
            return null;
          }
        });
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    CacheValue get(final String key) {
      try {
        return txRequired(new Callable<CacheValue>() {
          @Override
          public CacheValue call() throws Exception {
            Element result = cache.get(key);
            return result == null ? null : (CacheValue) result.getObjectValue();
          }
        });
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    boolean removeIfPresent(final String key, final int value) {
      try {
        return txRequired(new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            Element oldElement = new Element(key, new CacheValue(value));
            return cache.removeElement(oldElement);
          }
        });
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    boolean increaseByOne(final String key) {
      try {
        return txRequired(new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            Element oldElement = cache.get(key);
            if (oldElement == null) {
              return false;
            }
            Element newElement = new Element(key, CacheValue.increaseByOne((CacheValue) oldElement.getObjectValue()));
            return cache.replace(oldElement, newElement);
          }
        });
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private <R> R txRequired(final Callable<R> task) throws Exception {
      final TransactionController txManager = cacheManager.getTransactionController();
      try {
        txManager.begin();
        return task.call();
      } finally {
        txManager.commit();
      }
    }

    public void close() {
      cacheManager.shutdown();
    }
  }

  static class TxCacheBuilder {

    static TxCache build() {
      final CacheManager cacheManager = new CacheManager(new Configuration());
      final CacheConfiguration cacheConfiguration = buildConfiguration();
      final Cache cache = new Cache(cacheConfiguration);
      cacheManager.addCache(cache);
      return new TxCache(cacheManager, cache);
    }

    static CacheConfiguration buildConfiguration() {
      final CacheConfiguration configuration = new CacheConfiguration();
      configuration.setTransactionalMode(CacheConfiguration.TransactionalMode.LOCAL.name());
      configuration.setCopyOnRead(true);
      configuration.setCopyOnWrite(true);
      configuration.setEternal(true);
      configuration.setMaxEntriesLocalHeap(100);
      configuration.setName("TestThreadSafetyCache");
      configuration.getCopyStrategyConfiguration().setClass("net.sf.ehcache.store.compound.ImmutableValueElementCopyStrategy");
      configuration.validateConfiguration();
      return configuration;
    }
  }

}
