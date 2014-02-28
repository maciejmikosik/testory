package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.zeroOrNull;

import java.util.List;

import org.junit.Test;

public class Describe_Classes_zeroOrNull {
  @Test
  public void returns_zero_for_primitives() {
    assertEquals(Boolean.valueOf(false), zeroOrNull(boolean.class));
    assertEquals(Character.valueOf((char) 0), zeroOrNull(char.class));
    assertEquals(Byte.valueOf((byte) 0), zeroOrNull(byte.class));
    assertEquals(Short.valueOf((short) 0), zeroOrNull(short.class));
    assertEquals(Integer.valueOf(0), zeroOrNull(int.class));
    assertEquals(Long.valueOf(0), zeroOrNull(long.class));
    assertEquals(Float.valueOf(0), zeroOrNull(float.class));
    assertEquals(Double.valueOf(0), zeroOrNull(double.class));
  }

  @Test
  public void returns_zero_for_wrappers() {
    assertEquals(Boolean.valueOf(false), zeroOrNull(Boolean.class));
    assertEquals(Character.valueOf((char) 0), zeroOrNull(Character.class));
    assertEquals(Byte.valueOf((byte) 0), zeroOrNull(Byte.class));
    assertEquals(Short.valueOf((short) 0), zeroOrNull(Short.class));
    assertEquals(Integer.valueOf(0), zeroOrNull(Integer.class));
    assertEquals(Long.valueOf(0), zeroOrNull(Long.class));
    assertEquals(Float.valueOf(0), zeroOrNull(Float.class));
    assertEquals(Double.valueOf(0), zeroOrNull(Double.class));
  }

  @Test
  public void returns_null_for_other_types() {
    assertNull(zeroOrNull(Number.class));
    assertNull(zeroOrNull(Void.class));
    assertNull(zeroOrNull(void.class));
    assertNull(zeroOrNull(Object.class));
    assertNull(zeroOrNull(String.class));
    assertNull(zeroOrNull(Exception.class));
    assertNull(zeroOrNull(List.class));
    assertNull(zeroOrNull(Runnable.class));
    assertNull(zeroOrNull(Class.class));
  }

  @Test
  public void type_cannot_be_null() {
    try {
      zeroOrNull(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
