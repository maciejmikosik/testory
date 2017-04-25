package org.testory.plumbing.inject;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.TestingMakers.assertFairDistribution;
import static org.testory.plumbing.inject.TestingMakers.population;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.plumbing.Maker;

public class TestFinalMaker {
  private String name, otherName;
  private int count;
  private Constructor<?> constructor;
  private Class<?> clazz, otherClazz;
  private Field field;
  private Method method;
  private Maker maker;

  @Before
  public void before() {
    maker = finalMaker();
    name = "name";
    otherName = "otherName";
    count = 50000;
  }

  @Test
  public void enums_are_fairly_distributed_among_all_elements() {
    List<TestEnum> population = population(count, maker, TestEnum.class, name);
    assertFairDistribution(TestEnum.values().length, population);
  }

  private static enum TestEnum {
    a, b, c, d, e, f, g, h, i, j
  }

  @Test
  public void strings_are_unique() {
    List<String> population = population(count, maker, String.class, name);
    assertFairDistribution(count, population);
  }

  @Test
  public void class_is_always_the_same() {
    clazz = maker.make(Class.class, name);
    otherClazz = maker.make(Class.class, otherName);
    assertSame(clazz, otherClazz);
  }

  @Test
  public void method_is_declared_in_sample_class() {
    method = maker.make(Method.class, name);
    clazz = maker.make(Class.class, otherName);
    assertSame(method.getDeclaringClass(), clazz);
  }

  @Test
  public void constructor_is_declared_in_sample_class() {
    constructor = maker.make(Constructor.class, name);
    clazz = maker.make(Class.class, otherName);
    assertSame(constructor.getDeclaringClass(), clazz);
  }

  @Test
  public void field_is_declared_in_sample_class() {
    field = maker.make(Field.class, name);
    clazz = maker.make(Class.class, otherName);
    assertSame(field.getDeclaringClass(), clazz);
  }

  @Test
  public void fails_for_unknown_type() {
    try {
      maker.make(Object.class, name);
      fail();
    } catch (RuntimeException e) {}
  }

  @Test
  public void fails_for_array_of_unknown_type() {
    try {
      maker.make(Object[].class, name);
      fail();
    } catch (RuntimeException e) {}
  }
}
