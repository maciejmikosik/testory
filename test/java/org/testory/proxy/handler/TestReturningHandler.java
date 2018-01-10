package org.testory.proxy.handler;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.handler.ReturningHandler.returning;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class TestReturningHandler {
  private Object object;
  private Invocation invocation;
  private Handler handler;

  @Before
  public void before() throws Throwable {
    object = new Object();
    invocation = invocation(Object.class.getMethod("toString"), new Object(), asList());
  }

  @Test
  public void returns_object() throws Throwable {
    handler = returning(object);
    assertEquals(
        object,
        handler.handle(invocation));
  }

  @Test
  public void returns_null() throws Throwable {
    handler = returning(null);
    assertEquals(
        null,
        handler.handle(invocation));
  }

  @Test
  public void implements_to_string() {
    handler = returning(object);
    assertEquals(
        format("returning(%s)", object.toString()),
        handler.toString());
  }

  @Test
  public void implements_to_string_with_null() {
    handler = returning(null);
    assertEquals(
        "returning(null)",
        handler.toString());
  }
}
