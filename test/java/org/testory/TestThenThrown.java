package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class TestThenThrown {
  private Throwable throwable;
  private Object object;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
  }

  @Test
  public void asserts_throwing() {
    when(throwing(throwable));
    thenThrown();
  }

  @Test
  public void fails_returning_object() {
    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    \n"
              + "  but returned\n"
              + "    %s\n",
              object),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_void() {
    when(voidReturning());
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    \n"
          + "  but returned\n"
          + "    void\n",
          e.getMessage());
    }
  }
}
