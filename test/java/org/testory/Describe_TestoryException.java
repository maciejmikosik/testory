package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.test.Testilities.here;
import static org.testory.test.Testilities.nextLine;

import org.junit.Test;

public class Describe_TestoryException {
  private StackTraceElement line;

  @Test
  public void reports_caller_code() {
    try {
      line = nextLine(here());
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertEquals("incorrect usage in client code", e.getMessage());
      assertEquals(1, e.getStackTrace().length);
      assertEquals(line, e.getStackTrace()[0]);
    }
  }

  @Test
  public void reports_failed_check() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      Throwable cause = e.getCause();
      assertEquals("failed precondition in testory code", cause.getMessage());
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
