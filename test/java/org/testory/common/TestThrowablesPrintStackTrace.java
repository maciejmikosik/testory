package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.testory.common.Throwables.printStackTrace;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

public class TestThrowablesPrintStackTrace {
  private String stackTrace;
  private Throwable throwable;

  @Before
  public void before() {
    stackTrace = "lineA\nlineB\nlineC";
  }

  @Test
  public void should_print_stack_trace() {
    throwable = new Throwable() {
      public void printStackTrace(PrintStream stream) {
        stream.append(stackTrace);
      }

      public void printStackTrace(PrintWriter writer) {
        writer.append(stackTrace);
      }
    };
    String printed = printStackTrace(throwable);
    assertEquals(stackTrace + "\n", printed);
  }
}
