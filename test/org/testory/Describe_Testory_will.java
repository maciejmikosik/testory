package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_Testory_will {
  private Will will;
  private Invocation invocation;
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void should_return_object() throws Throwable {
    will = willReturn(object);
    assertSame(object, will.handle(invocation));
  }

  @Test
  public void should_return_null() throws Throwable {
    will = willReturn(null);
    assertSame(null, will.handle(invocation));
  }

  @Test
  public void should_return_object_again() throws Throwable {
    will = willReturn(object);
    will.handle(invocation);
    assertSame(object, will.handle(invocation));
  }

  @Test
  public void should_throw_throwable() {
    will = willThrow(throwable);
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void should_throw_throwable_again() {
    will = willThrow(throwable);
    try {
      will.handle(invocation);
    } catch (Throwable e) {}
    try {
      will.handle(invocation);
      fail();
    } catch (Throwable e) {
      assertSame(throwable, e);
    }
  }

  @Test
  public void should_not_throw_null() {
    try {
      willThrow(null);
      fail();
    } catch (TestoryException e) {}
  }
}
