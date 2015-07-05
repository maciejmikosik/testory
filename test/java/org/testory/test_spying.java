package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.spy;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willSpy;
import static org.testory.testing.Testilities.newObject;
import static org.testory.testing.Testilities.newThrowable;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class test_spying {
  private Mockable mock, spy, real, otherMock;
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    real = new Mockable();
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void returns_from_real_object_method() {
    spy = spy(real);
    assertEquals(object, spy.doReturn(object));

    given(willSpy(real), onInstance(mock));
    assertEquals(object, mock.doReturn(object));
  }

  @Test
  public void throws_from_real_object_method() throws Throwable {
    spy = spy(real);
    try {
      spy.doThrow(throwable);
      fail();
    } catch (Throwable e) {
      assertSame(e, throwable);
    }

    given(willSpy(real), onInstance(mock));
    try {
      mock.doThrow(throwable);
      fail();
    } catch (Throwable e) {
      assertSame(e, throwable);
    }
  }

  @Test
  public void can_be_stubbed() {
    spy = spy(real);
    given(willReturn(object), onInstance(mock));
    assertSame(object, mock.doReturn(null));

    given(willSpy(real), onInstance(mock));
    given(willReturn(object), onInstance(mock));
    assertSame(object, mock.doReturn(null));
  }

  @Test
  public void can_be_verified() {
    spy = spy(real);
    spy.doReturn(object);
    thenCalled(onInstance(spy));

    given(willSpy(real), onInstance(mock));
    mock.doReturn(object);
    thenCalled(onInstance(mock));
  }

  @Test
  public void can_spy_another_mock() {
    spy = spy(otherMock);
    given(willReturn(object), onInstance(otherMock));
    assertEquals(object, spy.doReturn(null));

    given(willSpy(otherMock), onInstance(mock));
    given(willReturn(object), onInstance(otherMock));
    assertEquals(object, mock.doReturn(null));
  }

  @Test
  public void cannot_spy_null() {
    try {
      willSpy(null);
      fail();
    } catch (TestoryException e) {}

    try {
      spy(null);
      fail();
    } catch (TestoryException e) {}
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }
    };
  }

  private static class Mockable {
    Object doReturn(Object o) {
      return o;
    }

    Object doThrow(Throwable t) throws Throwable {
      throw t;
    }
  }
}
