package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;

import org.junit.Test;

public class Describe_TestoryException {
  private int line;

  @Test
  public void reports_caller_code() {
    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertEquals("incorrect usage", e.getMessage());
      assertEquals(1, e.getStackTrace().length);
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
    }
  }

  @Test
  public void reports_failed_check() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      Throwable cause = e.getCause();
      assertEquals("failed check", cause.getMessage());
      assertEquals(1, cause.getStackTrace().length);
      assertEquals(Testory.class.getName(), cause.getStackTrace()[0].getClassName());
      assertEquals("mock", cause.getStackTrace()[0].getMethodName());
    }
  }

  @Test
  public void reports_complete_stack_trace() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      Throwable cause = e.getCause().getCause();
      assertNull(cause.getMessage());
      assertTrue(cause.getStackTrace().length > 1);
      assertNull(cause.getCause());
    }
  }
}
