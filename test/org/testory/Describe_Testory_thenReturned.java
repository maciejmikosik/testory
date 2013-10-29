package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.printStackTrace;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_thenReturned {
  private Object object, otherObject;
  private Throwable throwable;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
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
  public void should_succeed_if_returned_object_equal_to_expected_object() {
    when(new Integer(10));
    thenReturned(new Integer(10));
  }

  @Test
  public void should_succeed_if_returned_object_same_as_expected_object() {
    when(object);
    thenReturned(object);
  }

  @Test
  public void should_fail_if_returned_object_not_equal_to_expected_object() {
    when(object);
    try {
      thenReturned(otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    otherObject\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_object() {
    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_array_equal_to_expected_object() {
    when(new Object[] { object });
    thenReturned(new Object[] { object });
  }

  @Test
  public void should_succeed_if_returned_null_equal_to_expected_null_object() {
    when((Object) null);
    thenReturned((Object) null);
  }

  @Test
  public void should_fail_if_returned_object_not_equal_to_expected_null_object() {
    when(object);
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_null_not_equal_to_expected_object() {
    when((Object) null);
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_object_matched_by_matcher() {
    when(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void should_fail_if_returned_object_not_matched_by_matcher() {
    when(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_object_matched_by_matcher() {
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_expected_primitive() {
    when(returning(4));
    thenReturned(4);
  }

  @Test
  public void should_fail_if_returned_not_expected_primitive() {
    when(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    5\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_null_instead_of_expected_primitive() {
    when(returning(null));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_primitive() {
    when(throwing(throwable));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_expected_null() {
    when(returning(null));
    thenReturned(null);
  }

  @Test
  public void should_fail_if_returned_not_expected_null() {
    when(returning(object));
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_null() {
    when(throwing(throwable));
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_when_expected_returned_anything() {
    when(returning(object));
    thenReturned();
  }

  @Test
  public void should_succeed_if_returned_null_when_expected_returned_anything() {
    when(returning(null));
    thenReturned();
  }

  @Test
  public void should_fail_if_thrown_when_expected_returned_anything() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    \n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }
}
