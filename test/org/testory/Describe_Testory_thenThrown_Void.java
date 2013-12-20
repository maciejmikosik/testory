package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.test.Testilities.Invoker;

public class Describe_Testory_thenThrown_Void {
  private Throwable throwable;
  private Object object;
  private Invoker invoker;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_closure_thrown() {
    when(throwing(throwable));
    thenThrown();
  }

  @Test
  public void should_succeed_if_proxy_thrown() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    thenThrown();
  }

  @Test
  public void should_fail_if_closure_returned() {
    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    \n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned() throws Throwable {
    when(invoker).invoke(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    \n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }
}
