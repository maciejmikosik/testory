package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.ProxyException;

public class TestStubbing {
  private Object object, otherObject;
  private Throwable throwable;
  private Invocation invocation;
  private InvocationMatcher onAlways, onNever, invocationMatcher;
  private Handler handler;
  private Mockable mock;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
    onAlways = new InvocationMatcher() {
      public boolean matches(Invocation inv) {
        return true;
      }
    };
    onNever = new InvocationMatcher() {
      public boolean matches(Invocation inv) {
        return false;
      }
    };
    invocationMatcher = mock(InvocationMatcher.class);
    handler = mock(Handler.class);
    mock = mock(Mockable.class);
    given(willThrow(new RuntimeException("unstubbed")), onAlways);
  }

  @Test
  public void mock_returns_object() {
    given(willReturn(object), onAlways);
    assertSame(object, mock.returnObject());
  }

  @Test
  public void mock_returns_primitive() {
    given(willReturn(5), onAlways);
    assertEquals(5, mock.returnInt());
  }

  @Test
  public void mock_returns_null() {
    given(willReturn(null), onAlways);
    assertEquals(null, mock.returnObject());
  }

  @Test
  public void mock_returns_void() {
    given(willReturn(null), onAlways);
    mock.returnVoid();
  }

  @Test
  public void mock_throws_error() {
    throwable = new Error();
    given(willThrow(throwable), onAlways);
    try {
      mock.returnObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void mock_throws_runtime_exception() {
    throwable = new RuntimeException();
    given(willThrow(throwable), onAlways);
    try {
      mock.returnObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void mock_throws_declared_throwable() {
    given(willThrow(throwable), onAlways);
    try {
      mock.throwThrowable();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void mock_throws_declared_exception() {
    throwable = new IOException();
    given(willThrow(throwable), onAlways);
    try {
      mock.throwIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void mock_throws_subclass_of_declared_exception() {
    throwable = new FileNotFoundException();
    given(willThrow(throwable), onAlways);
    try {
      mock.throwIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void stubbing_cannot_return_super_type() {
    given(willReturn(object), onAlways);
    try {
      mock.returnString();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_return_wider_primitive() {
    given(willReturn(3f), onAlways);
    try {
      mock.returnInt();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_return_object_as_void() {
    given(willReturn(object), onAlways);
    try {
      mock.returnVoid();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_return_object_as_primitive() {
    given(willReturn(object), onAlways);
    try {
      mock.returnInt();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_return_null_as_primitive() {
    given(willReturn(null), onAlways);
    try {
      mock.returnInt();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_not_throw_undeclared_exception() {
    throwable = new IOException();
    given(willThrow(throwable), onAlways);
    try {
      mock.returnObject();
      fail();
    } catch (ProxyException t) {}
  }

  @Test
  public void stubbing_cannot_throw_undeclared_throwable() {
    given(willThrow(throwable), onAlways);
    try {
      mock.returnObject();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void stubbing_cannot_throw_superclass_of_declared_exception() throws IOException {
    throwable = new Exception();
    given(willThrow(throwable), onAlways);
    try {
      mock.throwIOException();
      fail();
    } catch (ProxyException t) {}
  }

  @Test
  public void mock_uses_last_stubbing() {
    given(willReturn(object), onAlways);
    given(willReturn(otherObject), onAlways);
    assertSame(otherObject, mock.returnObject());
  }

  @Test
  public void mock_uses_last_matching_stubbing() {
    given(willReturn(object), onAlways);
    given(willReturn(otherObject), onNever);
    assertSame(object, mock.returnObject());
  }

  @Test
  public void mock_does_not_use_stubbing_preceding_its_creation() {
    given(willReturn(object), onAlways);
    mock = mock(Mockable.class);
    assertNotSame(object, mock.returnObject());
  }

  @Test
  public void invocation_matcher_matches_invocation_on_mock() throws NoSuchMethodException {
    given(new Handler() {
      public Object handle(Invocation inv) {
        invocation = inv;
        return null;
      }
    }, onAlways);
    mock.acceptObject(object);
    assertSame(mock, invocation.instance);
    assertEquals(Mockable.class.getDeclaredMethod("acceptObject", Object.class), invocation.method);
    assertEquals(Arrays.asList(object), invocation.arguments);
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      given(handler, (InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_handler_is_not_null() {
    try {
      given(null, invocationMatcher);
      fail();
    } catch (TestoryException e) {}
  }

  private static abstract class Mockable {
    abstract Object returnObject();

    abstract String returnString();

    abstract int returnInt();

    abstract void returnVoid();

    abstract void throwThrowable() throws Throwable;

    abstract void throwIOException() throws IOException;

    abstract Object acceptObject(Object argument);
  }
}
