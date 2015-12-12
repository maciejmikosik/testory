package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.Matchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_thrown_type {
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
    } catch (TestoryAssertionError e) {}
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
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_object() {
    class ExpectedThrowable extends Throwable {}
    when(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void() {
    class ExpectedThrowable extends Throwable {}
    when(voidReturning());
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_throwable() {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    throwable = new OtherThrowable();
    when(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"));
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
