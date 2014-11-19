package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Classes.canAssign;

import org.junit.Before;
import org.junit.Test;

public class describe_Classes_canAssign {
  private Boolean aBool;
  private Character aChar;
  private Byte aByte;
  private Short aShort;
  private Integer aInt;
  private Long aLong;
  private Float aFloat;
  private Double aDouble;

  @Before
  public void before() {
    aBool = false;
    aChar = 0;
    aByte = 0;
    aShort = 0;
    aInt = 0;
    aLong = 0L;
    aFloat = 0f;
    aDouble = 0.0;
  }

  @Test
  public void can_assign_to_same_class() {
    class Klass {}
    assertTrue(canAssign(new Klass(), Klass.class));
  }

  @Test
  public void can_assign_to_superclass() {
    class Superclass {}
    class MyClass extends Superclass {}
    assertTrue(canAssign(new MyClass(), Superclass.class));
  }

  @Test
  public void cannot_assign_to_subclass() {
    class MyClass {}
    class Subclass extends MyClass {}
    assertFalse(canAssign(new MyClass(), Subclass.class));
  }

  @Test
  public void can_assign_to_object() {
    class MyClass {}
    assertTrue(canAssign(new MyClass(), Object.class));
  }

  @Test
  public void can_convert_primitives() {
    assertTrue(canAssign(aByte, byte.class));
    assertTrue(canAssign(aByte, short.class));
    assertTrue(canAssign(aByte, int.class));
    assertTrue(canAssign(aByte, long.class));
    assertFalse(canAssign(aShort, byte.class));
    assertTrue(canAssign(aShort, short.class));
    assertTrue(canAssign(aShort, int.class));
    assertTrue(canAssign(aShort, long.class));
    assertFalse(canAssign(aInt, byte.class));
    assertFalse(canAssign(aInt, short.class));
    assertTrue(canAssign(aInt, int.class));
    assertTrue(canAssign(aInt, long.class));
    assertFalse(canAssign(aLong, byte.class));
    assertFalse(canAssign(aLong, short.class));
    assertFalse(canAssign(aLong, int.class));
    assertTrue(canAssign(aLong, long.class));

    assertTrue(canAssign(aFloat, float.class));
    assertTrue(canAssign(aFloat, double.class));
    assertFalse(canAssign(aDouble, float.class));
    assertTrue(canAssign(aDouble, double.class));

    for (Object value : asList(aByte, aShort, aInt, aLong)) {
      for (Class<?> type : asList(float.class, double.class)) {
        assertTrue(formatMessage(value, type), canAssign(value, type));
      }
    }
    for (Object value : asList(aFloat, aDouble)) {
      for (Class<?> type : asList(byte.class, short.class, int.class, long.class)) {
        assertFalse(formatMessage(value, type), canAssign(value, type));
      }
    }

    assertTrue(canAssign(aChar, char.class));
    assertFalse(canAssign(aByte, char.class));
    assertFalse(canAssign(aShort, char.class));
    assertFalse(canAssign(aInt, char.class));
    assertFalse(canAssign(aLong, char.class));
    assertFalse(canAssign(aFloat, char.class));
    assertFalse(canAssign(aDouble, char.class));
    assertFalse(canAssign(aChar, byte.class));
    assertFalse(canAssign(aChar, short.class));
    assertTrue(canAssign(aChar, int.class));
    assertTrue(canAssign(aChar, long.class));
    assertTrue(canAssign(aChar, float.class));
    assertTrue(canAssign(aChar, double.class));

    assertTrue(canAssign(aBool, boolean.class));
    assertFalse(canAssign(aChar, boolean.class));
    assertFalse(canAssign(aByte, boolean.class));
    assertFalse(canAssign(aShort, boolean.class));
    assertFalse(canAssign(aInt, boolean.class));
    assertFalse(canAssign(aLong, boolean.class));
    assertFalse(canAssign(aFloat, boolean.class));
    assertFalse(canAssign(aDouble, boolean.class));
    assertFalse(canAssign(aBool, char.class));
    assertFalse(canAssign(aBool, byte.class));
    assertFalse(canAssign(aBool, short.class));
    assertFalse(canAssign(aBool, int.class));
    assertFalse(canAssign(aBool, long.class));
    assertFalse(canAssign(aBool, float.class));
    assertFalse(canAssign(aBool, double.class));
  }

  @Test
  public void cannot_convert_wrappers() {
    for (Object value : asList(aBool, aChar, aByte, aShort, aInt, aLong, aFloat, aDouble)) {
      for (Class<?> type : asList(Boolean.class, Character.class, Byte.class, Short.class,
          Integer.class, Long.class, Float.class, Double.class)) {
        assertEquals(formatMessage(value, type), value.getClass() == type, canAssign(value, type));
      }
    }
  }

  @Test
  public void cannot_assign_null_to_primitive() {
    assertFalse(canAssign(null, boolean.class));
    assertFalse(canAssign(null, char.class));
    assertFalse(canAssign(null, byte.class));
    assertFalse(canAssign(null, short.class));
    assertFalse(canAssign(null, int.class));
    assertFalse(canAssign(null, long.class));
    assertFalse(canAssign(null, float.class));
    assertFalse(canAssign(null, double.class));
  }

  @Test
  public void can_assign_null_to_wrapper() {
    assertTrue(canAssign(null, Boolean.class));
    assertTrue(canAssign(null, Character.class));
    assertTrue(canAssign(null, Byte.class));
    assertTrue(canAssign(null, Short.class));
    assertTrue(canAssign(null, Integer.class));
    assertTrue(canAssign(null, Long.class));
    assertTrue(canAssign(null, Float.class));
    assertTrue(canAssign(null, Double.class));
  }

  @Test
  public void can_assign_only_null_to_only_wrapper_void() {
    assertTrue(canAssign(null, Void.class));
    assertFalse(canAssign(null, void.class));

    assertFalse(canAssign(new Object(), Void.class));
    assertFalse(canAssign(new Object(), void.class));
    assertFalse(canAssign(aInt, Void.class));
    assertFalse(canAssign(aInt, void.class));
  }

  private static String formatMessage(Object value, Class<?> type) {
    return "can assign " + value.getClass().getName() + " to type " + type.getName();
  }
}
