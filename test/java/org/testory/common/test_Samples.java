package org.testory.common;

import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Samples.sample;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class test_Samples {
  private String seed, otherSeed;
  private int count;
  private List<Object> population = new ArrayList<>();
  private Constructor<?> sampleConstructor;
  private Class<?> sampleClass, otherSampleClass;
  private Field sampleField;
  private Method sampleMethod;

  @Before
  public void before() {
    seed = "seed";
    otherSeed = "otherSeed";
    count = 50000;
  }

  @Test
  public void booleans_are_fairly_distributed() {
    for (Class<?> type : asList(boolean.class, Boolean.class)) {
      population = population(count, type, seed);
      assertUniqueCount(2, population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void characters_are_fairly_distributed_among_lower_case_letters() {
    for (Class<?> type : asList(char.class, Character.class)) {
      population = population(count, type, seed);
      assertUniqueCount('z' - 'a' + 1, population);
      assertWithinRange('a', 'z', population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void bytes_are_fairly_distributed_within_range() {
    for (Class<?> type : asList(byte.class, Byte.class)) {
      population = population(count, type, seed);
      assertUniqueCount(8, population);
      assertWithinRange((byte) -5, (byte) 5, population);
      assertOutsideRange((byte) -1, (byte) 1, population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void shorts_are_fairly_distributed_within_range() {
    for (Class<?> type : asList(short.class, Short.class)) {
      population = population(count, type, seed);
      assertUniqueCount(31 * 2 - 2, population);
      assertWithinRange((short) -31, (short) 31, population);
      assertOutsideRange((short) -1, (short) 1, population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void integers_are_fairly_distributed_withing_range() {
    for (Class<?> type : asList(int.class, Integer.class)) {
      population = population(count, type, seed);
      assertUniqueCount(1290 * 2 - 2 - 100, population);
      assertWithinRange(-1290, 1290, population);
      assertOutsideRange(-1, 1, population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void longs_are_fairly_distributed_withing_range() {
    for (Class<?> type : asList(long.class, Long.class)) {
      population = population(count, type, seed);
      assertUniqueCount(count / 2, population);
      assertWithinRange(-2097152L, 2097152L, population);
      assertOutsideRange(-1L, 1L, population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void floats_are_fairly_distributed_withing_range() {
    for (Class<?> type : asList(float.class, Float.class)) {
      population = population(count, type, seed);
      assertUniqueCount(count / 2, population);
      assertWithinRange((float) -pow(2, 30), (float) pow(2, 30), population);
      assertOutsideRange((float) -pow(2, -30), (float) pow(2, -30), population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void doubles_are_fairly_distributed_withing_range() {
    for (Class<?> type : asList(double.class, Double.class)) {
      population = population(count, type, seed);
      assertUniqueCount(count / 2, population);
      assertWithinRange(-pow(2, 300), pow(2, 300), population);
      assertOutsideRange(-pow(2, -300), pow(2, -300), population);
      assertFairDistribution(population);
    }
  }

  @Test
  public void enums_are_fairly_distributed_among_all_elements() {
    population = population(count, TestEnum.class, seed);
    assertUniqueCount(TestEnum.values().length, population);
    assertFairDistribution(population);
  }

  private static enum TestEnum {
    a, b, c, d, e, f, g, h, i, j
  }

  @Test
  public void strings_are_unique() {
    population = population(count, String.class, seed);
    assertUniqueCount(count, population);
  }

  @Test
  public void class_is_always_the_same() {
    sampleClass = sample(Class.class, seed);
    otherSampleClass = sample(Class.class, otherSeed);
    assertSame(sampleClass, otherSampleClass);
  }

  @Test
  public void method_is_declared_in_sample_class() {
    sampleMethod = sample(Method.class, seed);
    sampleClass = sample(Class.class, otherSeed);
    assertSame(sampleMethod.getDeclaringClass(), sampleClass);
  }

  @Test
  public void constructor_is_declared_in_sample_class() {
    sampleConstructor = sample(Constructor.class, seed);
    sampleClass = sample(Class.class, otherSeed);
    assertSame(sampleConstructor.getDeclaringClass(), sampleClass);
  }

  @Test
  public void field_is_declared_in_sample_class() {
    sampleField = sample(Field.class, seed);
    sampleClass = sample(Class.class, otherSeed);
    assertSame(sampleField.getDeclaringClass(), sampleClass);
  }

  private static List<Object> population(int count, Class<?> type, String seed) {
    List<Object> samples = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      samples.add(sample(type, seed + i));
    }
    return samples;
  }

  private static void assertUniqueCount(int count, List<?> elements) {
    int uniqueCount = new HashSet<>(elements).size();
    String message = "count was " + uniqueCount + ", expected " + count;
    assertTrue(message, areOrdered(count, uniqueCount));
  }

  private static void assertWithinRange(Object lower, Object upper, List<?> elements) {
    for (Object element : elements) {
      String message = element + " is not within <" + lower + ", " + upper + ">";
      assertTrue(message, areOrdered(lower, element));
      assertTrue(message, areOrdered(element, upper));
    }
  }

  private static void assertOutsideRange(Object lower, Object upper, List<?> elements) {
    for (Object element : elements) {
      assertTrue(areOrdered(element, lower) || areOrdered(upper, element));
    }
  }

  private static boolean areOrdered(Object lower, Object upper) {
    return ((Comparable<Object>) lower).compareTo(upper) <= 0;
  }

  private static void assertFairDistribution(List<?> elements) {
    Map<Object, Integer> histogram = histogram(elements);
    int fairCount = Math.round(elements.size() / histogram.size());
    int minimalCount = Math.round(1f / 3 * fairCount);
    int maximalCount = Math.round(3f * fairCount);
    for (Entry<Object, Integer> entry : histogram.entrySet()) {
      int frequency = entry.getValue();
      String message = "frequency of " + entry.getKey() + " was " + frequency
          + ", expected around " + fairCount;
      assertTrue(message, minimalCount <= frequency);
      assertTrue(message, frequency <= maximalCount);
    }
  }

  private static Map<Object, Integer> histogram(List<?> elements) {
    Map<Object, Integer> histogram = new HashMap<>();
    for (Object element : elements) {
      if (!histogram.containsKey(element)) {
        histogram.put(element, 0);
      }
      histogram.put(element, histogram.get(element) + 1);
    }
    return histogram;
  }
}
