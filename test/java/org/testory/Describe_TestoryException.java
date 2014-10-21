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
  private StackTraceElement trace;

  @Test
  public void message_conforms_to_template() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().matches("" //
          + "\n" //
          + "  illegal testory usage\n" //
          + "  failed precondition\n" //
          + "    .*\n"));
    }
  }

  @Test
  public void message_contains_failed_precondition() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), !e.getMessage().contains("check"));
      assertTrue(e.getMessage(), e.getMessage().contains("type != null"));
    }
  }

  @Test
  public void reports_caller_trace() {
    try {
      line = nextLine(here());
      mock(null);
      fail();
    } catch (TestoryException e) {
      trace = e.getStackTrace()[1];
      assertEquals(this.getClass().getName(), trace.getClassName());
      assertEquals(line, trace);
    }
  }

  @Test
  public void reports_failed_precondition_trace() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      trace = e.getStackTrace()[0];
      assertEquals(Testory.class.getName(), trace.getClassName());
      assertEquals("mock", trace.getMethodName());
    }
  }

  @Test
  public void reports_complete_stack_trace() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      Throwable cause = e.getCause();
      assertNull(cause.getMessage());
      assertTrue(cause.getStackTrace().length > 2);
      assertNull(cause.getCause());
    }
  }
}
