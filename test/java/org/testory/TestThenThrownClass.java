package org.testory;

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

public class TestThenThrownClass {
  private Throwable throwable;
  private Object object;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
  }

  @Test
  public void asserts_throwing_same_type() {
    class ExpectedThrowable extends Throwable {}
    when(throwing(new ExpectedThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void asserts_throwing_subtype() {
    class ExpectedThrowable extends Throwable {}
    class SubThrowable extends ExpectedThrowable {}
    when(throwing(new SubThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void fails_throwing_supertype() {
    class SuperThrowable extends Throwable {}
    class ExpectedThrowable extends SuperThrowable {}
    throwable = new SuperThrowable();
    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable.getClass().getName() + "\n"
          + "\n"
          + printStackTrace(throwable),
          e.getMessage());
    }
  }

  @Test
  public void fails_throwing_unrelated_type() {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    throwable = new OtherThrowable();
    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable.getClass().getName() + "\n"
          + "\n"
          + printStackTrace(throwable),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_object() {
    class ExpectedThrowable extends Throwable {}
    when(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but returned\n"
          + "    " + object + "\n",
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_void() {
    class ExpectedThrowable extends Throwable {}
    when(voidReturning());
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but returned\n"
          + "    void\n",
          e.getMessage());
    }
  }

  @Test
  public void type_cannot_be_null() {
    try {
      thenThrown((Class<? extends Throwable>) null);
      fail();
    } catch (TestoryException e) {}
  }
}
