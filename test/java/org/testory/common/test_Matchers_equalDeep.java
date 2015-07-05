package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class test_Matchers_equalDeep {
  private Object object, equal, other;

  @Before
  public void before() {
    object = newObject("object");
    equal = newObject("object");
    other = newObject("other");
  }

  @Test
  public void requires_equal_object() {
    // assume
    assertEquals(object, equal);
    assertEquals(equal, object);
    assertNotEquals(object, other);
    assertNotEquals(other, object);
    // test
    assertTrue(equalDeep(object).matches(equal));
    assertFalse(equalDeep(object).matches(other));
  }

  @Test
  public void compares_arrays_as_values() {
    assertTrue(equalDeep(new int[] { 1, 2, 3 }).matches(new int[] { 1, 2, 3 }));
    assertFalse(equalDeep(new int[] { 1, 2, 3 }).matches(new int[] { 1, 0, 3 }));
    assertTrue(equalDeep(new Object[] { object }).matches(new Object[] { equal }));
    assertFalse(equalDeep(new Object[] { object }).matches(new Object[] { other }));
  }

  @Test
  public void handles_null() {
    assertTrue(equalDeep(null).matches(null));
    assertFalse(equalDeep(object).matches(null));
    assertFalse(equalDeep(null).matches(object));
  }

  @Test
  public void prints() {
    assertEquals("equalDeep(" + object + ")", equalDeep(object).toString());
  }
}
