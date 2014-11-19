package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class describe_asserting {
  private Object object, otherObject;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void asserts_true_condition() {
    then(true);
  }

  @Test
  public void fails_for_false_condition() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    true\n" //
          + "  but was\n" //
          + "    false\n" //
      , e.getMessage());
    }
  }

  @Test
  public void asserts_matching_object() {
    matcher = matcherSame(object);
    then(object, matcher);
  }

  @Test
  public void fails_for_not_matching_object() {
    matcher = matcherSame(object);
    try {
      then(otherObject, matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    " + matcher + "\n" //
          + "  but was\n" //
          + "    " + otherObject + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void asserts_equal_but_not_same_object() {
    thenEqual(new Integer(10), new Integer(10));
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
      assertEquals("\n" //
          + "  expected\n" //
          + "    " + object + "\n" //
          + "  but was\n" //
          + "    " + otherObject + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_non_null_object_if_expected_equal_to_null() {
    try {
      thenEqual(object, null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    null\n" //
          + "  but was\n" //
          + "    " + object + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_null_if_expected_non_null_object() {
    try {
      thenEqual(null, object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    " + object + "\n" //
          + "  but was\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void matcher_cannot_be_any_object() {
    try {
      then(object, otherObject);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      then(object, null);
      fail();
    } catch (TestoryException e) {}
  }

  private static Object matcherSame(final Object expected) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == expected;
      }

      public String toString() {
        return "matcherSame(" + expected + ")";
      }
    };
  }
}
