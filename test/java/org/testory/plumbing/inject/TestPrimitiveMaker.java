package org.testory.plumbing.inject;

import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertThat;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.inject.TestingMakers.assertFairDistribution;
import static org.testory.plumbing.inject.TestingMakers.population;
import static org.testory.plumbing.inject.TestingMakers.withinRange;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.plumbing.Maker;

public class TestPrimitiveMaker {
  private String namePrefix;
  private int count;
  private Maker maker;

  @Before
  public void before() {
    maker = randomPrimitiveMaker();
    namePrefix = "namePrefix";
    count = 80000;
  }

  @Test
  public void booleans_are_fairly_distributed() {
    for (Class<Boolean> type : asList(boolean.class, Boolean.class)) {
      List<Boolean> population = population(count, maker, type, namePrefix);
      assertFairDistribution(2, population);
    }
  }

  @Test
  public void characters_are_fairly_distributed_among_lower_case_letters() {
    for (Class<Character> type : asList(char.class, Character.class)) {
      List<Character> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(withinRange('a', 'z')));
      assertFairDistribution('z' - 'a' + 1, population);
    }
  }

  @Test
  public void bytes_are_fairly_distributed_within_range() {
    for (Class<Byte> type : asList(byte.class, Byte.class)) {
      List<Byte> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange((byte) -5, (byte) -2),
          withinRange((byte) 2, (byte) 5))));
      assertFairDistribution(8, population);
    }
  }

  @Test
  public void shorts_are_fairly_distributed_within_range() {
    for (Class<Short> type : asList(short.class, Short.class)) {
      List<Short> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange((short) -31, (short) -2),
          withinRange((short) 2, (short) 31))));
      assertFairDistribution(31 * 2 - 2, population);
    }
  }

  @Test
  public void integers_are_fairly_distributed_within_range() {
    for (Class<Integer> type : asList(int.class, Integer.class)) {
      List<Integer> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange(-1290, -2),
          withinRange(2, 1290))));
      assertFairDistribution(1290 * 2 - 2 - 100, population);
    }
  }

  @Test
  public void longs_are_fairly_distributed_within_range() {
    for (Class<Long> type : asList(long.class, Long.class)) {
      List<Long> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange(-2097152L, -2L),
          withinRange(2L, 2097152L))));
      assertFairDistribution(count / 2, population);
    }
  }

  @Test
  public void floats_are_fairly_distributed_within_range() {
    for (Class<Float> type : asList(float.class, Float.class)) {
      List<Float> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange((float) -pow(2, 30), (float) -pow(2, -30)),
          withinRange((float) pow(2, -30), (float) pow(2, 30)))));
      assertFairDistribution(count / 2, population);
    }
  }

  @Test
  public void doubles_are_fairly_distributed_within_range() {
    for (Class<Double> type : asList(double.class, Double.class)) {
      List<Double> population = population(count, maker, type, namePrefix);
      assertThat(population, everyItem(anyOf(
          withinRange(-pow(2, 300), -pow(2, -300)),
          withinRange(pow(2, -300), pow(2, 300)))));
      assertFairDistribution(count / 2, population);
    }
  }
}
