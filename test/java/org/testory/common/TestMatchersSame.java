package org.testory.common;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.testory.common.Matchers.same;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestMatchersSame {
  private Object object, other, equal;

  @Before
  public void before() {
    object = newObject("object");
    equal = newObject("object");
    other = newObject("other");
  }

  @Test
  public void requires_same_instance() {
    // assume
    assertNotSame(object, other);
    assertNotEquals(object, other);
    assertNotEquals(other, object);
    // test
    assertTrue(same(object).matches(object));
    assertFalse(same(object).matches(other));

  }

  @Test
  public void ignores_equals() {
    // assume
    assertNotSame(object, equal);
    assertEquals(object, equal);
    assertEquals(equal, object);
    // test
    assertFalse(same(object).matches(equal));
  }

  @Test
  public void handles_null() {
    // assume
    assertNotNull(object);
    // test
    assertTrue(same(null).matches(null));
    assertFalse(same(null).matches(object));
    assertFalse(same(object).matches(null));
  }

  @Test
  public void prints() {
    assertEquals(format("same(%s)", object), same(object).toString());
  }
}
