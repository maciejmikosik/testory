package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_then {
  private Object object, otherObject;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == object;
      }

      public String toString() {
        return "matcher";
      }
    };
  }

  @Test
  public void should_succeed_for_matching_object() {
    then(object, matcher);
  }

  @Test
  public void should_fail_for_not_matching_object() {
    try {
      then(otherObject, matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected\n" //
          + "    matcher\n" //
          + "  but was\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_for_not_matcher() {
    try {
      then(object, otherObject);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_for_null_matcher() {
    try {
      then(object, null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_succeed_for_true() {
    then(true);
  }

  @Test
  public void should_fail_for_false() {
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
}
