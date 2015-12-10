package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.StackTraces.printStackTrace;

import org.junit.Before;
import org.junit.Test;

public class test_when_closure {
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void inspects_closure_returning() {
    when(returning(object));
    thenReturned(object);
  }

  @Test
  public void inspects_closure_throwing() {
    when(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void failure_prints_inspected_closure_returning() {
    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  but returned\n"
          + "    " + object + "\n"));
    }
  }

  @Test
  public void failure_prints_inspected_closure_throwing() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  but thrown\n"
          + "    " + throwable + "\n"));
      assertTrue(e.getMessage(), e.getMessage().contains("\n" + printStackTrace(throwable) + "\n"));
    }
  }

  @Test
  public void closure_cannot_be_null() {
    try {
      when((Closure) null);
      fail();
    } catch (TestoryException e) {}
  }
}
