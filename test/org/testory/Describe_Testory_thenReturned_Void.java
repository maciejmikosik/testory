package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.printStackTrace;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.test.Testilities.Invoker;

public class Describe_Testory_thenReturned_Void {
  private Object object;
  private Throwable throwable;
  private Invoker invoker;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_returned() {
    when(object);
    thenReturned();
  }

  @Test
  public void should_succeed_if_closure_returned() {
    when(returning(object));
    thenReturned();
  }

  @Test
  public void should_succeed_if_proxy_returned() {
    when(returning(object));
    thenReturned();
  }

  @Test
  public void should_succeed_if_returned_null() {
    when((Object) null);
    thenReturned();
  }

  @Test
  public void should_succeed_if_closure_returned_null() {
    when(returning(null));
    thenReturned();
  }

  @Test
  public void should_succeed_if_proxy_returned_null() throws Throwable {
    when(invoker).invoke(returning(null));
    thenReturned();
  }

  @Test
  public void should_fail_if_closure_thrown_anything() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    \n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown_anything() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    \n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }
}
