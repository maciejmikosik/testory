package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenEqual;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestThenEqual {
  private Object object, equalObject, otherObject;

  @Before
  public void before() {
    object = newObject("object");
    equalObject = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void asserts_equal_but_not_same_object() {
    thenEqual(object, equalObject);
  }

  @Test
  public void asserts_equal_and_same_object() {
    thenEqual(object, object);
  }

  @Test
  public void asserts_equal_nulls() {
    thenEqual(null, null);
  }

  @Test
  public void fails_for_not_equal_object() {
    try {
      thenEqual(otherObject, object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected\n"
          + "    " + object + "\n"
          + "  but was\n"
          + "    " + otherObject + "\n",
          e.getMessage());
    }
  }

  @Test
  public void fails_for_non_null_object_if_expected_null() {
    try {
      thenEqual(object, null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected\n"
          + "    " + null + "\n"
          + "  but was\n"
          + "    " + object + "\n",
          e.getMessage());
    }
  }

  @Test
  public void fails_for_null_if_expected_non_null_object() {
    try {
      thenEqual(null, object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected\n"
          + "    " + object + "\n"
          + "  but was\n"
          + "    " + null + "\n",
          e.getMessage());
    }
  }
}
