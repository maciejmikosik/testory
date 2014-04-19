package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalled;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_TestoryAssertionError {
  private int line;
  private InvocationMatcher onNothing;

  @Before
  public void before() {
    onNothing = new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return false;
      }
    };
  }

  @Test
  public void reports_caller_code() {
    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
      assertEquals(Describe_TestoryAssertionError.class.getName(),
          e.getStackTrace()[0].getClassName());
    }

    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
      assertEquals(Describe_TestoryAssertionError.class.getName(),
          e.getStackTrace()[0].getClassName());
    }

    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      thenCalled(mock(ArrayList.class)).toString();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
      assertEquals(Describe_TestoryAssertionError.class.getName(),
          e.getStackTrace()[0].getClassName());
    }
  }

  @Test
  public void reports_complete_stack_trace() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      Throwable cause = e.getCause();
      assertNull(cause.getMessage());
      assertTrue(cause.getStackTrace().length > 1);
      assertNull(cause.getCause());
    }

    try {
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      Throwable cause = e.getCause();
      assertNull(cause.getMessage());
      assertTrue(cause.getStackTrace().length > 1);
      assertNull(cause.getCause());
    }

    try {
      thenCalled(mock(ArrayList.class)).toString();
      fail();
    } catch (TestoryAssertionError e) {
      Throwable cause = e.getCause();
      assertNull(cause.getMessage());
      assertTrue(cause.getStackTrace().length > 1);
      assertNull(cause.getCause());
    }
  }
}
