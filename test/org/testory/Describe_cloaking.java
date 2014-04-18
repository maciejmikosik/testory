package org.testory;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenThrown;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_cloaking {
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
  public void cloaks_at_calling_testory() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
      assertEquals(Describe_cloaking.class.getName(), e.getStackTrace()[0].getClassName());
    }

    try {
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(1, e.getStackTrace().length);
      assertEquals(Describe_cloaking.class.getName(), e.getStackTrace()[0].getClassName());
    }
  }

  @Test
  public void points_to_line_number_in_caller() {
    line = -1;
    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
    }

    line = -1;
    try {
      line = new Exception().getStackTrace()[0].getLineNumber();
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(line + 1, e.getStackTrace()[0].getLineNumber());
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
