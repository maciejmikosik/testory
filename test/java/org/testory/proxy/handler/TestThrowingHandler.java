package org.testory.proxy.handler;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.handler.ThrowingHandler.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.ProxyException;

public class TestThrowingHandler {
  private Throwable throwable;
  private Invocation invocation;
  private Handler handler;

  @Before
  public void before() throws Throwable {
    throwable = new TestException();
    invocation = invocation(Object.class.getMethod("toString"), new Object(), asList());
  }

  @Test
  public void throws_throwable() throws Throwable {
    handler = throwing(throwable);
    try {
      handler.handle(invocation);
      fail();
    } catch (TestException e) {}
  }

  @Test
  public void cannot_throw_null() throws Throwable {
    try {
      handler = throwing(null);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void implements_to_string() {
    handler = throwing(throwable);
    assertEquals(
        format("throwing(%s)", throwable.toString()),
        handler.toString());
  }

  private static class TestException extends Exception {}
}
