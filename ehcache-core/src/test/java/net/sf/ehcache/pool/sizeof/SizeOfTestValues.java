/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.ehcache.pool.sizeof;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cdennis
 */
// Update for versions > 8
public final class SizeOfTestValues {
  
  private static final Map<JvmInformation, Map<String, Long>> CORRECT_SIZES = new EnumMap<JvmInformation, Map<String, Long>>(JvmInformation.class);
  static {
    Map<String, Long> hotspot32Bit = new HashMap<String, Long>();
    hotspot32Bit.put("sizeOf(new Object())", 8L);
    hotspot32Bit.put("sizeOf(new Integer(1))", 16L);
    hotspot32Bit.put("sizeOf(1000)", 16L);
    hotspot32Bit.put("deepSizeOf(new SomeClass(false))", 16L);
    hotspot32Bit.put("deepSizeOf(new SomeClass(true))", 24L);
    hotspot32Bit.put("sizeOf(new Object[] { })", 16L);
    hotspot32Bit.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 32L);
    hotspot32Bit.put("sizeOf(new int[] { })", 16L);
    hotspot32Bit.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 32L);
    hotspot32Bit.put("deepSizeOf(new Pair(null, null))", 16L);
    hotspot32Bit.put("deepSizeOf(new Pair(new Object(), null))", 24L);
    hotspot32Bit.put("deepSizeOf(new Pair(new Object(), new Object()))", 32L);
    hotspot32Bit.put("deepSizeOf(new ReentrantReadWriteLock())", 112L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_32_BIT, hotspot32Bit);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_32_BIT, hotspot32Bit);

    Map<String, Long> hotspot32BitWithConcurrentMarkAndSweep = new HashMap<String, Long>();
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new Object())", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new Integer(1))", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(1000)", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new SomeClass(false))", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new SomeClass(true))", 24L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new Object[] { })", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 32L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new int[] { })", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 32L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new Pair(null, null))", 16L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new Pair(new Object(), null))", 24L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new Pair(new Object(), new Object()))", 32L);
    hotspot32BitWithConcurrentMarkAndSweep.put("deepSizeOf(new ReentrantReadWriteLock())", 112L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP, hotspot32BitWithConcurrentMarkAndSweep);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP, hotspot32BitWithConcurrentMarkAndSweep);

    Map<String, Long> hotspot64Bit = new HashMap<String, Long>();
    hotspot64Bit.put("sizeOf(new Object())", 16L);
    hotspot64Bit.put("sizeOf(new Integer(1))", 24L);
    hotspot64Bit.put("sizeOf(1000)", 24L);
    hotspot64Bit.put("deepSizeOf(new SomeClass(false))", 24L);
    hotspot64Bit.put("deepSizeOf(new SomeClass(true))", 40L);
    hotspot64Bit.put("sizeOf(new Object[] { })", 24L);
    hotspot64Bit.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 56L);
    hotspot64Bit.put("sizeOf(new int[] { })", 24L);
    hotspot64Bit.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 40L);
    hotspot64Bit.put("deepSizeOf(new Pair(null, null))", 32L);
    hotspot64Bit.put("deepSizeOf(new Pair(new Object(), null))", 48L);
    hotspot64Bit.put("deepSizeOf(new Pair(new Object(), new Object()))", 64L);
    hotspot64Bit.put("deepSizeOf(new ReentrantReadWriteLock())", 192L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_64_BIT, hotspot64Bit);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_64_BIT, hotspot64Bit);

    Map<String, Long> hotspot64BitWithCMS = new HashMap<String, Long>();
    hotspot64BitWithCMS.put("sizeOf(new Object())", 24L);
    hotspot64BitWithCMS.put("sizeOf(new Integer(1))", 24L);
    hotspot64BitWithCMS.put("sizeOf(1000)", 24L);
    hotspot64BitWithCMS.put("deepSizeOf(new SomeClass(false))", 24L);
    hotspot64BitWithCMS.put("deepSizeOf(new SomeClass(true))", 40L);
    hotspot64BitWithCMS.put("sizeOf(new Object[] { })", 24L);
    hotspot64BitWithCMS.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 40L);
    hotspot64BitWithCMS.put("sizeOf(new int[] { })", 24L);
    hotspot64BitWithCMS.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 40L);
    hotspot64BitWithCMS.put("deepSizeOf(new Pair(null, null))", 32L);
    hotspot64BitWithCMS.put("deepSizeOf(new Pair(new Object(), null))", 48L);
    hotspot64BitWithCMS.put("deepSizeOf(new Pair(new Object(), new Object()))", 64L);
    hotspot64BitWithCMS.put("deepSizeOf(new ReentrantReadWriteLock())", 192L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP, hotspot64BitWithCMS);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP, hotspot64BitWithCMS);

    Map<String, Long> hotspot64BitWithCompressedOops = new HashMap<String, Long>();
    hotspot64BitWithCompressedOops.put("sizeOf(new Object())", 16L);
    hotspot64BitWithCompressedOops.put("sizeOf(new Integer(1))", 16L);
    hotspot64BitWithCompressedOops.put("sizeOf(1000)", 16L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new SomeClass(false))", 16L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new SomeClass(true))", 32L);
    hotspot64BitWithCompressedOops.put("sizeOf(new Object[] { })", 16L);
    hotspot64BitWithCompressedOops.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 32L);
    hotspot64BitWithCompressedOops.put("sizeOf(new int[] { })", 16L);
    hotspot64BitWithCompressedOops.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 32L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new Pair(null, null))", 24L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new Pair(new Object(), null))", 40L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new Pair(new Object(), new Object()))", 56L);
    hotspot64BitWithCompressedOops.put("deepSizeOf(new ReentrantReadWriteLock())", 120L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS, hotspot64BitWithCompressedOops);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_64_BIT_WITH_COMPRESSED_OOPS, hotspot64BitWithCompressedOops);

    Map<String, Long> hotspot64BitWithCompressedOopsAndCMS = new HashMap<String, Long>();
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new Object())", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new Integer(1))", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(1000)", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new SomeClass(false))", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new SomeClass(true))", 32L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new Object[] { })", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 32L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new int[] { })", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 32L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new Pair(null, null))", 24L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new Pair(new Object(), null))", 40L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new Pair(new Object(), new Object()))", 56L);
    hotspot64BitWithCompressedOopsAndCMS.put("deepSizeOf(new ReentrantReadWriteLock())", 120L);

    CORRECT_SIZES.put(JvmInformation.HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP, hotspot64BitWithCompressedOopsAndCMS);
    CORRECT_SIZES.put(JvmInformation.OPENJDK_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP, hotspot64BitWithCompressedOopsAndCMS);

    Map<String, Long> ibm32Bit = new HashMap<String, Long>();
    ibm32Bit.put("sizeOf(new Object())", 16L);
    ibm32Bit.put("sizeOf(new Integer(1))", 16L);
    ibm32Bit.put("sizeOf(1000)", 16L);
    ibm32Bit.put("deepSizeOf(new SomeClass(false))", 16L);
    ibm32Bit.put("deepSizeOf(new SomeClass(true))", 32L);
    ibm32Bit.put("sizeOf(new Object[] { })", 16L);
    ibm32Bit.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 32L);
    ibm32Bit.put("sizeOf(new int[] { })", 16L);
    ibm32Bit.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 32L);
    ibm32Bit.put("deepSizeOf(new Pair(null, null))", 24L);
    ibm32Bit.put("deepSizeOf(new Pair(new Object(), null))", 40L);
    ibm32Bit.put("deepSizeOf(new Pair(new Object(), new Object()))", 56L);
    CORRECT_SIZES.put(JvmInformation.IBM_32_BIT, ibm32Bit);
    
    Map<String, Long> ibm64Bit = new HashMap<String, Long>();
    ibm64Bit.put("sizeOf(new Object())", 24L);
    ibm64Bit.put("sizeOf(new Integer(1))", 32L);
    ibm64Bit.put("sizeOf(1000)", 32L);
    ibm64Bit.put("deepSizeOf(new SomeClass(false))", 32L);
    ibm64Bit.put("deepSizeOf(new SomeClass(true))", 56L);
    ibm64Bit.put("sizeOf(new Object[] { })", 24L);
    ibm64Bit.put("sizeOf(new Object[] { new Object(), new Object(), new Object(), new Object() })", 56L);
    ibm64Bit.put("sizeOf(new int[] { })", 24L);
    ibm64Bit.put("sizeOf(new int[] { 987654, 876543, 765432, 654321 })", 40L);
    ibm64Bit.put("deepSizeOf(new Pair(null, null))", 40L);
    ibm64Bit.put("deepSizeOf(new Pair(new Object(), null))", 64L);
    ibm64Bit.put("deepSizeOf(new Pair(new Object(), new Object()))", 88L);
    CORRECT_SIZES.put(JvmInformation.IBM_64_BIT, ibm64Bit);
    
    CORRECT_SIZES.put(JvmInformation.IBM_64_BIT_WITH_COMPRESSED_REFS, Collections.<String, Long>emptyMap());

    CORRECT_SIZES.put(JvmInformation.UNKNOWN_32_BIT, Collections.<String, Long>emptyMap());
    CORRECT_SIZES.put(JvmInformation.UNKNOWN_64_BIT, Collections.<String, Long>emptyMap());
    
    for (JvmInformation jvm : JvmInformation.values()) {
      Assert.assertThat(CORRECT_SIZES, IsMapContaining.hasKey(jvm));
    }
  }
  
  public static Long get(String expression) {
    return CORRECT_SIZES.get(JvmInformation.CURRENT_JVM_INFORMATION).get(expression);
  }
  
  private SizeOfTestValues() {
    //not instantiable
  }
}
