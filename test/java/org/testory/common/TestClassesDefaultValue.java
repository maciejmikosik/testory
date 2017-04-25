package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.defaultValue;

import java.util.List;

import org.junit.Test;

public class TestClassesDefaultValue {
  @Test
  public void returns_binary_zero_for_primitives() {
    assertEquals(Boolean.valueOf(false), defaultValue(boolean.class));
    assertEquals(Character.valueOf((char) 0), defaultValue(char.class));
    assertEquals(Byte.valueOf((byte) 0), defaultValue(byte.class));
    assertEquals(Short.valueOf((short) 0), defaultValue(short.class));
    assertEquals(Integer.valueOf(0), defaultValue(int.class));
    assertEquals(Long.valueOf(0), defaultValue(long.class));
    assertEquals(Float.valueOf(0), defaultValue(float.class));
    assertEquals(Double.valueOf(0), defaultValue(double.class));
  }

  @Test
  public void returns_null_for_wrappers() {
    assertNull(defaultValue(Boolean.class));
    assertNull(defaultValue(Character.class));
    assertNull(defaultValue(Byte.class));
    assertNull(defaultValue(Short.class));
    assertNull(defaultValue(Integer.class));
    assertNull(defaultValue(Long.class));
    assertNull(defaultValue(Float.class));
    assertNull(defaultValue(Double.class));
  }

  @Test
  public void returns_null_for_other_types() {
    assertNull(defaultValue(Number.class));
    assertNull(defaultValue(Void.class));
    assertNull(defaultValue(void.class));
    assertNull(defaultValue(Object.class));
    assertNull(defaultValue(String.class));
    assertNull(defaultValue(Exception.class));
    assertNull(defaultValue(List.class));
    assertNull(defaultValue(Runnable.class));
    assertNull(defaultValue(Class.class));
  }

  @Test
  public void type_cannot_be_null() {
    try {
      defaultValue(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
