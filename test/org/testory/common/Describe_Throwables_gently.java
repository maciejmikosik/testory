package org.testory.common;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.common.Throwables.gently;

import org.junit.Test;

public class Describe_Throwables_gently {
  private Throwable throwable;

  @Test
  public void should_propagate_runtime_exception() {
    throwable = new RuntimeException();
    try {
      gently(throwable);
      fail();
    } catch (RuntimeException e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void should_propagate_error() {
    throwable = new Error();
    try {
      gently(throwable);
      fail();
    } catch (Error e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void should_wrap_throwable() {
    throwable = new Throwable();
    try {
      gently(throwable);
      fail();
    } catch (RuntimeException e) {
      assertSame(throwable, e.getCause());
    }
  }

  @Test
  public void should_fail_for_null() {
    try {
      gently(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
