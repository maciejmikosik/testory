package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class TestThenThrownThrowable {
  private Throwable throwable, otherThrowable;
  private Object object;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
  }

  @Test
  public void asserts_throwing_same_throwable() {
    when(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void fails_throwing_not_same_throwable() {
    when(throwing(otherThrowable));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but thrown\n"
              + "    %s\n"
              + "\n"
              + "%s",
              throwable,
              otherThrowable,
              printStackTrace(otherThrowable)),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_object() {
    when(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but returned\n"
              + "    %s\n",
              throwable,
              object),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_void() {
    when(voidReturning());
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but returned\n"
              + "    void\n",
              throwable),
          e.getMessage());
    }
  }

  @Test
  public void throwable_cannot_be_null() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {}
  }
}
