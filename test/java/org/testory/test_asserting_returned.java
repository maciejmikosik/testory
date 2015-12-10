package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_returned {
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
    // TODO replace by VoidClosure
    when(new Runnable() {
      public void run() {}
    }).run();
    thenReturned();
  }

  @Test
  public void fails_throwing() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  expected returned\n"
          + "    \n"));
    }
  }
}
