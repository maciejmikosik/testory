package org.testory.plumbing.inject;

import static org.junit.Assert.assertArrayEquals;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;

import org.junit.Before;
import org.junit.Test;
import org.testory.plumbing.Maker;

public class test_ArrayMaker {
  private String name;
  private Maker maker;
  private Object[] made;
  private String element;

  @Before
  public void before() {
    name = "name";
    element = "name";
  }

  @Test
  public void creates_singleton_array() {
    maker = singletonArray(makerOf(element));
    made = maker.make(String[].class, name);
    assertArrayEquals(new String[] { element }, made);
  }

  @Test
  public void creates_deep_singleton_array() {
    maker = singletonArray(makerOf(element));
    made = maker.make(String[][].class, name);
    assertArrayEquals(new String[][] { { element } }, made);
  }

  private static Maker makerOf(final Object element) {
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        return (T) element;
      }
    };
  }
}
