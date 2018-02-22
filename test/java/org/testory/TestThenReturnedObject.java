package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class TestThenReturnedObject {
  private String name;
  private Object object, otherObject;
  private Throwable throwable;

  @Before
  public void before() {
    name = "name";
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
  }

  @Test
  public void asserts_returning_equal_object() {
    when(returning(newObject(name)));
    thenReturned(newObject(name));
  }

  @Test
  public void asserts_returning_same_object() {
    when(returning(object));
    thenReturned(object);
  }

  @Test
  public void asserts_returning_equal_primitive() {
    when(returning(4));
    thenReturned(4);
  }

  @Test
  public void asserts_returning_equal_null() {
    when(returning(null));
    thenReturned((Object) null);
  }

  @Test
  public void fails_returning_not_equal_object() {
    when(returning(otherObject));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but returned\n"
              + "    %s\n",
              object,
              otherObject),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_not_equal_primitive() {
    when(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    4\n"
          + "  but returned\n"
          + "    5\n",
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_object_instead_of_null() {
    when(returning(object));
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    null\n"
              + "  but returned\n"
              + "    %s\n",
              object),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_null_instead_of_object() {
    when(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but returned\n"
              + "    null\n",
              object),
          e.getMessage());
    }
  }

  @Test
  public void fails_throwing() {
    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but thrown\n"
              + "    %s\n"
              + "\n"
              + "%s",
              object,
              throwable,
              printStackTrace(throwable)),
          e.getMessage());
    }
  }
}
