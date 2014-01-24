package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.printStackTrace;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.test.Testilities.Invoker;

public class Describe_Testory_thenThrown_Throwable {
  private Throwable throwable, otherThrowable;
  private Object object;
  private Invoker invoker;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_closure_thrown_same_throwable() {
    when(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void should_succeed_if_proxy_thrown_same_throwable() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void should_fail_if_closure_thrown_not_same_throwable() {
    when(throwing(otherThrowable));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but thrown\n" //
          + "    otherThrowable\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown_not_same_throwable() throws Throwable {
    when(invoker).invoke(throwing(otherThrowable));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but thrown\n" //
          + "    otherThrowable\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned() {
    when(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned() throws Throwable {
    when(invoker).invoke(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_void() {
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but returned\n" //
          + "    void\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_expected_null_throwable() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {}
  }
}
