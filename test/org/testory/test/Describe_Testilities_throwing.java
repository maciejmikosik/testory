package org.testory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class Describe_Testilities_throwing {
  private Throwable throwable;
  private Closure closure;

  @Before
  public void before() {
    throwable = new Throwable();
  }

  @Test
  public void should_throw_throwable() throws Throwable {
    closure = throwing(throwable);
    try {
      closure.invoke();
      fail();
    } catch (Throwable e) {
      assertEquals(throwable, e);
    }
  }

  @Test
  public void should_fail_throwing_null() {
    try {
      throwing(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
