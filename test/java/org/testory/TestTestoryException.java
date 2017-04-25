package org.testory;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.HamcrestMatchers.hasMessageMatching;
import static org.testory.testing.Purging.triggerPurge;
import static org.testory.testing.StackTraces.here;
import static org.testory.testing.StackTraces.nextLine;

import org.junit.Test;
import org.testory.facade.Facade;

public class TestTestoryException {
  private StackTraceElement line;
  private StackTraceElement trace;
  private Object mock;

  @Test
  public void message_conforms_to_template() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessageMatching(""
          + "\n"
          + "  illegal testory usage\n"
          + "  failed precondition\n"
          + "    .*\n"));
    }
  }

  @Test
  public void message_contains_failed_precondition() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, not(hasMessageContaining("check")));
      assertThat(e, hasMessageContaining("type != null"));
    }
  }

  @Test
  public void message_explains_purged_mock() {
    mock = mock(Object.class);
    triggerPurge();
    try {
      mock.toString();
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessageContaining("isMock"));
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
      assertEquals(Facade.class.getName(), trace.getClassName());
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
