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

public class Describe_Testory_thenThrown_Object {
  private Throwable throwable, otherThrowable;
  private Object object;
  private Object matcher;
  private Invoker invoker;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == throwable;
      }

      public String toString() {
        return "matcher";
      }
    };
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_closure_thrown_matched() {
    when(throwing(throwable));
    thenThrown(matcher);
  }

  @Test
  public void should_succeed_if_proxy_thrown_matched() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    thenThrown(matcher);
  }

  @Test
  public void should_fail_if_closure_thrown_mismatched() {
    when(throwing(otherThrowable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
          + "  but thrown\n" //
          + "    otherThrowable\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown_mismatched() throws Throwable {
    when(invoker).invoke(throwing(otherThrowable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
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
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned() throws Throwable {
    when(invoker).invoke(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_expected_object() {
    try {
      thenThrown(object);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_if_expected_null() {
    try {
      thenThrown((Object) null);
      fail();
    } catch (TestoryException e) {}
  }
}
