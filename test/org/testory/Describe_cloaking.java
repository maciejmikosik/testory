package org.testory;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.Testory.thenThrown;

import org.junit.Test;

public class Describe_cloaking {
  @Test
  public void stack_trace_contains_one_element() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
    }
  }

  @Test
  public void stack_trace_contains_caller_class_name() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(Describe_cloaking.class.getName(), e.getStackTrace()[0].getClassName());
    }
  }

  @Test
  public void stack_trace_contains_line_number_in_caller_class() {
    int previousLine = -1;
    try {
      previousLine = new Exception().getStackTrace()[0].getLineNumber();
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(previousLine + 1, e.getStackTrace()[0].getLineNumber());
    }
  }

  @Test
  public void exceptions_are_not_cloaked() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e.getStackTrace().length, greaterThan(1));
    }
  }
}
