package jdk.java.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testory.testing.Testilities.newObject;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

public class learn_Objects_equals {
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
    assertTrue(Objects.equals(object, otherObject));
  }

  @Test
  public void should_match_same_equal_objects() {
    assertTrue(Objects.equals(object, object));
  }

  @Test
  public void should_not_match_not_equal_objects() {
    assertFalse(Objects.equals(object, otherObject));
  }

  @Test
  public void should_null_not_match_object() {
    assertFalse(Objects.equals(null, object));
  }

  @Test
  public void should_object_not_match_null() {
    assertFalse(Objects.equals(object, null));
  }

  @Test
  public void should_match_nulls() {
    assertTrue(Objects.equals(null, null));
  }

  @Test
  public void should_not_match_equal_arrays() {
    object = new int[] { 1, 2, 3, 4 };
    otherObject = new int[] { 1, 2, 3, 4 };
    assertFalse(Objects.equals(object, otherObject));
  }

  @Test
  public void should_array_not_match_object() {
    object = new int[] { 1, 2, 3, 4 };
    assertFalse(Objects.equals(object, otherObject));
  }

  @Test
  public void should_object_not_match_array() {
    otherObject = new int[] { 1, 2, 3, 4 };
    assertFalse(Objects.equals(object, otherObject));
  }

  @Test
  public void should_array_not_match_null() {
    object = new Object[] { otherObject };
    assertFalse(Objects.equals(object, null));
  }

  @Test
  public void should_null_not_match_array() {
    object = new Object[] { otherObject };
    assertFalse(Objects.equals(null, object));
  }
}
