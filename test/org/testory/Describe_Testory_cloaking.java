package org.testory;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.Testory.thenThrown;

import org.junit.Test;

public class Describe_Testory_cloaking {
  @Test
  public void should_error_contain_one_element() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
    }
  }

  @Test
  public void should_error_be_from_test_class() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(Describe_Testory_cloaking.class.getName(), e.getStackTrace()[0].getClassName());
    }
  }

  @Test
  public void should_error_be_from_correct_line() {
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
  public void should_exception_contain_original_stacktrace() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e.getStackTrace().length, greaterThan(1));
    }
  }
}
