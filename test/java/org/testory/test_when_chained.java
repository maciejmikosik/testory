package org.testory;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
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
import org.testory.testing.Closures.Invoker;

public class test_when_chained {
  private Object object;
  private Throwable throwable;
  private Runnable runnable;
  private Invoker invoker;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
    invoker = new Invoker();
    runnable = new Runnable() {
      public void run() {}
    };
  }

  @Test
  public void inspects_method_returning() throws Throwable {
    when(invoker).invoke(returning(object));
    thenReturned(object);
  }

  @Test
  public void inspects_method_returning_void() throws Throwable {
    when(runnable).run();
    thenReturned();
  }

  @Test
  public void inspects_method_throwing() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void failure_prints_inspected_method_returning() throws Throwable {
    when(invoker).invoke(returning(object));
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
  public void failure_prints_inspected_method_returning_void() {
    when(runnable).run();
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  but returned\n"
          + "    void\n"));
    }
  }

  @Test
  public void failure_prints_inspected_method_throwing() throws Throwable {
    when(invoker).invoke(throwing(throwable));
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
  public void chaining_does_not_propagate_throwable() throws Throwable {
    when(invoker).invoke(throwing(throwable));
  }

  @Test
  public void chaining_works_for_non_final_class() {
    class Foo {}
    object = new Foo();
    Object when = when(object);
    assertNotSame(object, when);
    assertTrue(when instanceof Foo);
  }

  @Test
  public void chaining_does_not_work_for_final_class() {
    final class FinalClass {}
    FinalClass finalInstance = new FinalClass();
    FinalClass when = when(finalInstance);
    assertNull(when);
  }

  @Test
  public void chaining_does_not_work_for_null_instance() {
    Object when = when((Object) null);
    assertNull(when);
  }
}
