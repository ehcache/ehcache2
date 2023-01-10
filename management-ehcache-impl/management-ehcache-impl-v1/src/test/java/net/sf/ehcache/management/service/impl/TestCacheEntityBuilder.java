/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package net.sf.ehcache.management.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import net.sf.ehcache.management.resource.CacheEntity;
import net.sf.ehcache.management.sampled.CacheSampler;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author brandony
 */
public class TestCacheEntityBuilder {

  @Test
  public void testMultipleSamplersForSingleCM() {
    String cmName = "CM1";

    CacheSampler samplerFoo = mock(CacheSampler.class);
    when(samplerFoo.getCacheName()).thenReturn("FOO");
    when(samplerFoo.getExpiredCount()).thenReturn(1L);
    when(samplerFoo.isLocalHeapCountBased()).thenReturn(true);

    CacheSampler samplerGoo = mock(CacheSampler.class);
    when(samplerGoo.getCacheName()).thenReturn("GOO");
    when(samplerGoo.getExpiredCount()).thenReturn(2L);
    when(samplerGoo.isLocalHeapCountBased()).thenReturn(true);

    CacheSampler samplerBar = mock(CacheSampler.class);
    when(samplerBar.getCacheName()).thenReturn("BAR");
    when(samplerBar.getExpiredCount()).thenReturn(3L);
    when(samplerBar.isLocalHeapCountBased()).thenReturn(true);

    CacheEntityBuilder ceb = CacheEntityBuilder.createWith(samplerFoo, cmName);
    ceb.add(samplerGoo, cmName);
    ceb.add(samplerBar, cmName);

    Set<String> constraintNames = new HashSet<String>(1);
    constraintNames.add("ExpiredCount");

    ceb.add(constraintNames);

    Collection<CacheEntity> ces = ceb.build();

    Assert.assertEquals(3, ces.size());

    Set<String> expectedCaches = new HashSet<String>(3);

    for(CacheEntity ce : ces) {
      expectedCaches.add(ce.getName());
      Assert.assertEquals(1, ce.getAttributes().size());

      Object value = ce.getAttributes().get("ExpiredCount");
      Assert.assertNotNull(value);

      Long expiredCount = Long.class.cast(value);

      if(expiredCount == 1L) {
        Assert.assertEquals("FOO", ce.getName());
      } else if (expiredCount == 2L) {
        Assert.assertEquals("GOO", ce.getName());
      } else if (expiredCount == 3L) {
        Assert.assertEquals("BAR", ce.getName());
      } else {
        Assert.fail("Unexpected attribute value!");
      }
    }

    Assert.assertEquals(3, expectedCaches.size());
  }
}
