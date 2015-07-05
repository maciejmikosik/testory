package org.testory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.willRethrow;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class test_will {
  private Handler will;
  private Invocation invocation;
  private Object object;
  private Throwable throwable;
  private StackTraceElement[] stackTraceA, stackTraceB, stackTraceC;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void returns_object() throws Throwable {
    will = willReturn(object);
    assertSame(object, will.handle(invocation));
    assertSame(object, will.handle(invocation));
  }

  @Test
  public void returns_null() throws Throwable {
    will = willReturn(null);
    assertSame(null, will.handle(invocation));
    assertSame(null, will.handle(invocation));
  }

  @Test
  public void throws_throwable() {
    will = willThrow(throwable);
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void throws_throwable_with_filled_in_stack_trace() throws Throwable {
    stackTraceA = throwable.getStackTrace();
    will = willThrow(throwable);
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable t) {
      stackTraceB = t.getStackTrace();
    }
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable t) {
      stackTraceC = t.getStackTrace();
    }
    assertFalse(Arrays.deepEquals(stackTraceA, stackTraceB));
    assertFalse(Arrays.deepEquals(stackTraceA, stackTraceC));
    assertFalse(Arrays.deepEquals(stackTraceB, stackTraceC));
  }

  @Test
  public void rethrows_throwable() {
    will = willRethrow(throwable);
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void rethrows_throwable_with_original_stack_trace() throws Throwable {
    stackTraceA = throwable.getStackTrace();
    will = willRethrow(throwable);
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable t) {
      stackTraceB = t.getStackTrace();
    }
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable t) {
      stackTraceC = t.getStackTrace();
    }
    assertArrayEquals(stackTraceA, stackTraceB);
    assertArrayEquals(stackTraceA, stackTraceC);
  }

  @Test
  public void cannot_throw_null() {
    try {
      willThrow(null);
      fail();
    } catch (TestoryException e) {}
    try {
      willRethrow(null);
      fail();
    } catch (TestoryException e) {}
  }
}
