package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.Matchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_thrown_throwable {
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
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_object() {
    when(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void() {
    // TODO replace by VoidClosure
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_throwable() {
    when(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected thrown\n"
          + "    " + throwable + "\n"));
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
