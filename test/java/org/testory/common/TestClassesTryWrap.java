package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.tryWrap;

import java.util.AbstractList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestClassesTryWrap {
  private List<Class<?>> wrappers;

  @Before
  public void before() {
    wrappers = asList(Void.class, Boolean.class, Character.class, Byte.class, Short.class,
        Integer.class, Long.class, Float.class, Double.class);
  }

  @Test
  public void wraps_primitive_types() throws ReflectiveOperationException {
    for (Class<?> wrapper : wrappers) {
      Class<?> primitive = (Class<?>) wrapper.getField("TYPE").get(null);
      assertEquals(wrapper, tryWrap(primitive));
    }
  }

  @Test
  public void ignores_wrapper_types() {
    for (Class<?> wrapper : wrappers) {
      assertEquals(wrapper, tryWrap(wrapper));
    }
  }

  @Test
  public void ignores_other_types() {
    for (Class<?> type : asList(Object.class, String.class, List.class, AbstractList.class)) {
      assertEquals(type, tryWrap(type));
    }
  }

  @Test
  public void type_cannot_be_null() {
    try {
      tryWrap(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
