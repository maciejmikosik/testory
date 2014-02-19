package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Describe_stubbing {
  private Object object, argument, otherArgument;
  private Throwable throwable;
  private Foo mock, otherMock;
  private Handler handler;
  private Captor captor;

  @Before
  public void before() {
    purge();

    object = newObject("object");
    argument = newObject("argument");
    otherArgument = newObject("otherArgument");
    throwable = newThrowable("throwable");
    mock = mock(Foo.class);
    otherMock = mock(Foo.class);
    handler = mock(Handler.class);
    captor = mock(Captor.class);
  }

  @After
  public void after() {
    purge();
  }

  private void purge() {
    when("");
    when("");
  }

  class Foo {
    Object getObject() {
      throw new RuntimeException();
    }

    Object getObject(Object o) {
      throw new RuntimeException();
    }

    Object getOtherObject(Object o) {
      throw new RuntimeException();
    }

    String getString() {
      throw new RuntimeException();
    }

    int getInt() {
      throw new RuntimeException();
    }

    void getVoid() {
      throw new RuntimeException();
    }

    void throwsIOException() throws IOException {
      throw new RuntimeException();
    }
  }

  @Test
  public void stubs_equal_invocation() {
    given(willReturn(object), mock).getObject(argument);
    assertSame(object, mock.getObject(argument));
  }

  @Test
  public void ignores_different_mock() {
    given(willReturn(object), mock).getObject(argument);
    assertNotSame(object, otherMock.getObject(argument));
  }

  @Test
  public void ignores_different_method() {
    given(willReturn(object), mock).getObject(argument);
    assertNotSame(object, mock.getOtherObject(argument));
  }

  @Test
  public void ignores_different_argument() {
    given(willReturn(object), mock).getObject(argument);
    assertNotSame(object, mock.getObject(otherArgument));
  }

  @Test
  public void matches_invocation_with_custom_logic() {
    given(willReturn(object), new Captor() {
      public boolean matches(Invocation invocation) {
        assume(invocation.method.getReturnType() == Object.class);
        return invocation.instance == mock;
      }
    });
    assertSame(object, mock.getObject(argument));
    assertNotSame(object, otherMock.getObject(argument));
  }

  @Test
  public void returns_object() {
    given(willReturn(object), mock).getObject();
    assertEquals(object, mock.getObject());
  }

  @Test
  public void returns_primitive() {
    given(willReturn(3), mock).getInt();
    assertEquals(3, mock.getInt());
  }

  @Test
  public void returns_null() {
    given(willReturn(null), mock).getObject();
    assertEquals(null, mock.getObject());
  }

  @Test
  public void returned_null_is_converted_to_void() {
    given(willReturn(null), mock).getVoid();
    mock.getVoid();
  }

  @Test
  public void does_not_return_incompatible_type() {
    given(willReturn(object), mock).getString();
    try {
      mock.getString();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void does_not_return_incompatible_primitive() {
    given(willReturn(3f), mock).getInt();
    try {
      mock.getInt();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void does_not_return_incompatible_null() {
    given(willReturn(null), mock).getInt();
    try {
      mock.getInt();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void does_not_return_incompatible_with_void() {
    given(willReturn(object), mock).getVoid();
    try {
      mock.getVoid();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void throws_error() {
    throwable = new Error();
    given(willThrow(throwable), mock).getObject();
    try {
      mock.getObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_runtime_exception() {
    throwable = new RuntimeException();
    given(willThrow(throwable), mock).getObject();
    try {
      mock.getObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_declared_exception() throws IOException {
    throwable = new IOException();
    given(willThrow(throwable), mock).throwsIOException();
    try {
      mock.throwsIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_subclass_of_declared_exception() throws IOException {
    throwable = new FileNotFoundException();
    given(willThrow(throwable), mock).throwsIOException();
    try {
      mock.throwsIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void does_not_throw_undeclared_exception() {
    throwable = new IOException();
    given(willThrow(throwable), mock).getObject();
    try {
      mock.getObject();
      fail();
    } catch (TestoryException t) {}
  }

  @Test
  public void does_not_throw_superclass_of_declared_exception() throws IOException {
    throwable = new Exception();
    given(willThrow(throwable), mock).throwsIOException();
    try {
      mock.throwsIOException();
      fail();
    } catch (TestoryException t) {}
  }

  @Test
  public void handler_cannot_be_null() {
    try {
      given(null, mock);
      fail();
    } catch (TestoryException e) {}
    try {
      given(null, captor);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_null() {
    try {
      given(handler, (Object) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_any_object() {
    try {
      given(handler, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void captor_cannot_be_null() {
    try {
      given(handler, (Captor) null);
      fail();
    } catch (TestoryException e) {}
  }

  private static void assume(boolean assumption) {
    if (!assumption) {
      throw new RuntimeException("wrong assumption");
    }
  }
}
