package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.testing.Testilities.printStackTrace;

import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

public class describe_Testilities_printStackTrace {
  private String string;
  private Throwable throwable;

  @Before
  public void before() {
    string = "string";
  }

  @Test
  public void should_print_stack_trace() {
    throwable = new Throwable() {
      public void printStackTrace(PrintWriter writer) {
        writer.write(string);
      }
    };
    assertEquals(string, printStackTrace(throwable));
  }

  @Test
  public void should_fail_for_null_throwable() {
    try {
      printStackTrace(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
