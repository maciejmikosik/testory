package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class TestThenReturned {
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void asserts_returning_object() {
    when(returning(object));
    thenReturned();
  }

  @Test
  public void asserts_returning_null() {
    when(returning(null));
    thenReturned();
  }

  @Test
  public void asserts_returning_void() {
    when(voidReturning());
    thenReturned();
  }

  @Test
  public void fails_throwing() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n"
          + "  expected returned\n"
          + "    \n"
          + "  but thrown\n"
          + "    %s\n"
          + "\n"
          + "%s",
          throwable,
          printStackTrace(throwable)),
          e.getMessage());
    }
  }
}
