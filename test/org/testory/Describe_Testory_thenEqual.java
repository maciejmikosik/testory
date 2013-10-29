package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenEqual;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_thenEqual {
  private Object object, expected;

  @Before
  public void before() {
    object = newObject("object");
    expected = newObject("expected");
  }

  @Test
  public void should_succeed_for_equal_not_same_object() {
    thenEqual(new Integer(10), new Integer(10));
  }

  @Test
  public void should_succeed_for_same_equal_object() {
    thenEqual(object, object);
  }

  @Test
  public void should_fail_for_not_equal_object() {
    try {
      thenEqual(object, expected);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    expected\n" //
          + "  but was\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_for_nulls() {
    object = null;
    expected = null;
    thenEqual(object, expected);
  }

  @Test
  public void should_fail_for_not_equal_to_null() {
    expected = null;
    try {
      thenEqual(object, expected);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    null\n" //
          + "  but was\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_for_not_equal_null() {
    object = null;
    try {
      thenEqual(object, expected);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    expected\n" //
          + "  but was\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }
}
