package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Describe_will {
  private Handler will;
  private Invocation invocation;
  private Object object;
  private Throwable throwable;

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
  public void cannot_throw_null() {
    try {
      willThrow(null);
      fail();
    } catch (TestoryException e) {}
  }
}
