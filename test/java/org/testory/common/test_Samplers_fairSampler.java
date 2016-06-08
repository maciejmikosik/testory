package org.testory.common;

import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.common.Samplers.fairSampler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

public class test_Samplers_fairSampler {
  private String seed, otherSeed;
  private int count;
  private Constructor<?> sampleConstructor;
  private Class<?> sampleClass, otherSampleClass;
  private Field sampleField;
  private Method sampleMethod;
  private Sampler sampler;

  @Before
  public void before() {
    sampler = fairSampler();
    seed = "seed";
    otherSeed = "otherSeed";
    count = 50000;
  }

  @Test
  public void booleans_are_fairly_distributed() {
    for (Class<Boolean> type : asList(boolean.class, Boolean.class)) {
      List<Boolean> population = population(count, sampler, type, seed);
      assertFairDistribution(2, population);
    }
  }

  @Test
  public void characters_are_fairly_distributed_among_lower_case_letters() {
    for (Class<Character> type : asList(char.class, Character.class)) {
      List<Character> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(withinRange('a', 'z')));
      assertFairDistribution('z' - 'a' + 1, population);
    }
  }

  @Test
  public void bytes_are_fairly_distributed_within_range() {
    for (Class<Byte> type : asList(byte.class, Byte.class)) {
      List<Byte> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange((byte) -5, (byte) -2),
          withinRange((byte) 2, (byte) 5))));
      assertFairDistribution(8, population);
    }
  }

  @Test
  public void shorts_are_fairly_distributed_within_range() {
    for (Class<Short> type : asList(short.class, Short.class)) {
      List<Short> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange((short) -31, (short) -2),
          withinRange((short) 2, (short) 31))));
      assertFairDistribution(31 * 2 - 2, population);
    }
  }

  @Test
  public void integers_are_fairly_distributed_within_range() {
    for (Class<Integer> type : asList(int.class, Integer.class)) {
      List<Integer> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange(-1290, -2),
          withinRange(2, 1290))));
      assertFairDistribution(1290 * 2 - 2 - 100, population);
    }
  }

  @Test
  public void longs_are_fairly_distributed_within_range() {
    for (Class<Long> type : asList(long.class, Long.class)) {
      List<Long> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange(-2097152L, -2L),
          withinRange(2L, 2097152L))));
      assertFairDistribution(count / 2, population);
    }
  }

  @Test
  public void floats_are_fairly_distributed_within_range() {
    for (Class<Float> type : asList(float.class, Float.class)) {
      List<Float> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange((float) -pow(2, 30), (float) -pow(2, -30)),
          withinRange((float) pow(2, -30), (float) pow(2, 30)))));
      assertFairDistribution(count / 2, population);
    }
  }

  @Test
  public void doubles_are_fairly_distributed_within_range() {
    for (Class<Double> type : asList(double.class, Double.class)) {
      List<Double> population = population(count, sampler, type, seed);
      assertThat(population, everyItem(anyOf(
          withinRange(-pow(2, 300), -pow(2, -300)),
          withinRange(pow(2, -300), pow(2, 300)))));
      assertFairDistribution(count / 2, population);
    }
  }

  @Test
  public void enums_are_fairly_distributed_among_all_elements() {
    List<TestEnum> population = population(count, sampler, TestEnum.class, seed);
    assertFairDistribution(TestEnum.values().length, population);
  }

  private static enum TestEnum {
    a, b, c, d, e, f, g, h, i, j
  }

  @Test
  public void strings_are_unique() {
    List<String> population = population(count, sampler, String.class, seed);
    assertFairDistribution(count, population);
  }

  @Test
  public void class_is_always_the_same() {
    sampleClass = sampler.sample(Class.class, seed);
    otherSampleClass = sampler.sample(Class.class, otherSeed);
    assertSame(sampleClass, otherSampleClass);
  }

  @Test
  public void method_is_declared_in_sample_class() {
    sampleMethod = sampler.sample(Method.class, seed);
    sampleClass = sampler.sample(Class.class, otherSeed);
    assertSame(sampleMethod.getDeclaringClass(), sampleClass);
  }

  @Test
  public void constructor_is_declared_in_sample_class() {
    sampleConstructor = sampler.sample(Constructor.class, seed);
    sampleClass = sampler.sample(Class.class, otherSeed);
    assertSame(sampleConstructor.getDeclaringClass(), sampleClass);
  }

  @Test
  public void field_is_declared_in_sample_class() {
    sampleField = sampler.sample(Field.class, seed);
    sampleClass = sampler.sample(Class.class, otherSeed);
    assertSame(sampleField.getDeclaringClass(), sampleClass);
  }

  private static <T> List<T> population(int count, Sampler sampler, Class<T> type, String seed) {
    List<T> samples = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      samples.add(sampler.sample(type, seed + i));
    }
    return samples;
  }

  private static void assertFairDistribution(int expectedUniqueElements, List<?> elements) {
    Map<Object, Integer> histogram = histogram(elements);
    if (histogram.size() < expectedUniqueElements) {
      fail(format("there was %s unique elements, expected at least %s",
          histogram.size(), expectedUniqueElements));
    }
    float averageCount = 1f * elements.size() / histogram.size();
    for (float frequency : histogram.values()) {
      assertThat(frequency, withinRange(
          averageCount / 3,
          averageCount * 3));
    }
  }

  private static Map<Object, Integer> histogram(List<?> elements) {
    Map<Object, Integer> histogram = new HashMap<>();
    for (Object element : elements) {
      histogram.put(element, zeroIfNull(histogram.get(element)) + 1);
    }
    return histogram;
  }

  private static int zeroIfNull(Integer value) {
    return value == null
        ? 0
        : value;
  }

  private static <T extends Comparable<T>> Matcher<T> withinRange(final T lower, final T upper) {
    return new TypeSafeMatcher<T>() {
      public void describeTo(Description description) {
        description.appendText(format("within range [%s, %s]", lower, upper));
      }

      protected boolean matchesSafely(T item) {
        return lower.compareTo(item) <= 0 && upper.compareTo(item) >= 0;
      }
    };
  }
}
