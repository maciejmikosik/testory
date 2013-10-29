package org.testory.test;

import static org.junit.Assert.assertEquals;
import static org.testory.test.Testilities.returning;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class Describe_Testilities_returning {
  private Object object;
  private Closure closure;

  @Before
  public void before() {
    object = new Object();
  }

  @Test
  public void should_return_object() throws Throwable {
    closure = returning(object);
    assertEquals(object, closure.invoke());
  }

  @Test
  public void should_return_null() throws Throwable {
    closure = returning(null);
    assertEquals(null, closure.invoke());
  }
}
