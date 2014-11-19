package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class describe_Objects_areEqualDeep {
  private Object object, otherObject;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void should_match_equal_objects() {
    object = new Integer(10);
    otherObject = new Integer(10);
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_same_equal_objects() {
    assertTrue(areEqualDeep(object, object));
  }

  @Test
  public void should_not_match_not_equal_objects() {
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_null_not_match_object() {
    assertFalse(areEqualDeep(null, object));
  }

  @Test
  public void should_object_not_match_null() {
    assertFalse(areEqualDeep(object, null));
  }

  @Test
  public void should_match_nulls() {
    assertTrue(areEqualDeep(null, null));
  }

  @Test
  public void should_match_equal_boolean_arrays() {
    object = new boolean[] { true, false, true, false };
    otherObject = new boolean[] { true, false, true, false };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_boolean_arrays() {
    object = new boolean[] { true, false, true, false };
    otherObject = new boolean[] { true, false, false, false };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_char_arrays() {
    object = new char[] { 'a', 'b', 'c', 'd' };
    otherObject = new char[] { 'a', 'b', 'c', 'd' };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_char_arrays() {
    object = new char[] { 'a', 'b', 'c', 'd' };
    otherObject = new char[] { 'a', 'b', 'x', 'd' };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_byte_arrays() {
    object = new byte[] { 1, 2, 3, 4 };
    otherObject = new byte[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_byte_arrays() {
    object = new byte[] { 1, 2, 3, 4 };
    otherObject = new byte[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_short_arrays() {
    object = new short[] { 1, 2, 3, 4 };
    otherObject = new short[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_short_arrays() {
    object = new short[] { 1, 2, 3, 4 };
    otherObject = new short[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_int_arrays() {
    object = new int[] { 1, 2, 3, 4 };
    otherObject = new int[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_int_arrays() {
    object = new int[] { 1, 2, 3, 4 };
    otherObject = new int[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_long_arrays() {
    object = new long[] { 1, 2, 3, 4 };
    otherObject = new long[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_long_arrays() {
    object = new long[] { 1, 2, 3, 4 };
    otherObject = new long[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_float_arrays() {
    object = new float[] { 1, 2, 3, 4 };
    otherObject = new float[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_float_arrays() {
    object = new float[] { 1, 2, 3, 4 };
    otherObject = new float[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_double_arrays() {
    object = new double[] { 1, 2, 3, 4 };
    otherObject = new double[] { 1, 2, 3, 4 };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_double_arrays() {
    object = new double[] { 1, 2, 3, 4 };
    otherObject = new double[] { 1, 2, 5, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_object_arrays() {
    object = new Object[] { "a", 1111, true, "d" };
    otherObject = new Object[] { "a", 1111, true, "d" };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_equal_null_arrays() {
    object = new Object[] { null, null, null, null };
    otherObject = new Object[] { null, null, null, null };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_not_equal_object_arrays() {
    object = new Object[] { "a", 1111, true, "d" };
    otherObject = new Object[] { "a", 8888, true, "d" };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_arrays_of_different_primitive_types() {
    object = new int[] { 1, 2, 3, 4 };
    otherObject = new byte[] { 1, 2, 3, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_primitive_array_not_match_equal_wrapper_array() {
    object = new int[] { 1, 2, 3, 4 };
    otherObject = new Integer[] { 1, 2, 3, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_wrapper_array_not_match_equal_primitive_array() {
    object = new Integer[] { 1, 2, 3, 4 };
    otherObject = new int[] { 1, 2, 3, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_array_not_match_object() {
    object = new int[] { 1, 2, 3, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_object_not_match_array() {
    otherObject = new int[] { 1, 2, 3, 4 };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_deeply_equal_arrays() {
    object = new String[][] { { "a", "b" }, { "c", "d" } };
    otherObject = new String[][] { { "a", "b" }, { "c", "d" } };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_deeply_not_equal_arrays() {
    object = new String[][] { { "a", "b" }, { "c", "d" } };
    otherObject = new String[][] { { "a", "b" }, { "c", "x" } };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_match_deeply_equal_primitive_arrays() {
    object = new int[][] { { 1, 2 }, { 3, 4 } };
    otherObject = new int[][] { { 1, 2 }, { 3, 4 } };
    assertTrue(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_not_match_deeply_not_equal_primitive_arrays() {
    object = new int[][] { { 1, 2 }, { 3, 4 } };
    otherObject = new int[][] { { 1, 2 }, { 3, 0 } };
    assertFalse(areEqualDeep(object, otherObject));
  }

  @Test
  public void should_array_not_match_null() {
    object = new Object[] { otherObject };
    assertFalse(areEqualDeep(object, null));
  }

  @Test
  public void should_null_not_match_array() {
    object = new Object[] { otherObject };
    assertFalse(areEqualDeep(null, object));
  }
}
