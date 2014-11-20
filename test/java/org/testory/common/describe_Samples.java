package org.testory.common;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static java.util.Collections.frequency;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Samples.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/*
 * TODO test samples of all supported types
 */
public class describe_Samples {
  private String seed;
  private List<Object> samples;
  private int population;

  @Before
  public void before() {
    population = 100;
    seed = "seed";
    samples = new ArrayList<Object>();
  }

  @Test
  public void booleans_are_distributed() {
    for (Class<?> type : asList(boolean.class, Boolean.class)) {
      for (int i = 0; i < population; i++) {
        samples.add(sample(type, seed + i));
      }
      assertTrue(frequency(samples, true) > 0.4 * population);
      assertTrue(frequency(samples, false) > 0.4 * population);
    }
  }

  @Test
  public void characters_contains_all_letters() {
    for (Class<?> type : asList(char.class, Character.class)) {
      for (int i = 0; i < population; i++) {
        samples.add(sample(type, seed + i));
      }
      for (char c = 'a'; c <= 'z'; c++) {
        assertTrue("" + c, samples.contains(c));
      }
    }
  }

  @Test
  public void bytes_contains_all_small_numbers() {
    for (Class<?> type : asList(byte.class, Byte.class)) {
      for (int i = 0; i < population; i++) {
        samples.add(sample(type, seed + i));
      }
      for (byte b = -5; b <= -2; b++) {
        assertTrue("" + b, samples.contains(b));
      }
      for (byte b = 2; b <= 5; b++) {
        assertTrue("" + b, samples.contains(b));
      }
    }
  }

  @Test
  public void floats_are_within_range() {
    for (Class<?> type : asList(float.class, Float.class)) {
      for (int i = 0; i < population; i++) {
        samples.add(sample(type, seed + i));
      }
      for (Object sample : samples) {
        Float abs = abs((Float) sample);
        assertTrue("" + sample, pow(2, -30) < abs && abs < pow(2, 30));
      }
    }
  }

  @Test
  public void floats_are_not_duplicated() {
    for (Class<?> type : asList(float.class, Float.class)) {
      for (int i = 0; i < population; i++) {
        samples.add(sample(type, seed + i));
      }
      assertEquals(population, new HashSet<Object>(samples).size());
    }
  }

  @Test
  public void enums_contains_all_fields() {
    for (int i = 0; i < population; i++) {
      samples.add(sample(TestEnum.class, seed + i));
    }
    for (TestEnum value : TestEnum.values()) {
      assertTrue("" + value, samples.contains(value));
    }
  }

  private static enum TestEnum {
    a, b, c, d, e, f, g, h, i, j
  }

  @Test
  public void strings_are_not_duplicated() {
    for (int i = 0; i < population; i++) {
      samples.add(sample(String.class, seed + i));
    }
    assertEquals(population, new HashSet<Object>(samples).size());
  }
}