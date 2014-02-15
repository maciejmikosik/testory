package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Describe_stubbing {
  private Object object;
  private Throwable throwable;
  private List<Object> mock, otherMock;
  private Handler handler;
  private Captor captor;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
    mock = mock(List.class);
    otherMock = mock(List.class);
    handler = mock(Handler.class);
    captor = mock(Captor.class);
  }

  @After
  public void purge_stubbings_so_they_do_not_affect_other_tests() {
    when("");
    when("");
  }

  @Test
  public void stubs_equal_invocation() {
    given(willReturn(object), mock).get(0);
    assertSame(object, mock.get(0));
  }

  @Test
  public void ignores_different_mock() {
    given(willReturn(object), mock).get(0);
    assertNotSame(object, otherMock.get(0));
  }

  @Test
  public void ignores_different_method() {
    given(willReturn(object), mock).get(0);
    assertNotSame(object, mock.remove(0));
  }

  @Test
  public void ignores_different_argument() {
    given(willReturn(object), mock).get(0);
    assertNotSame(object, mock.get(1));
  }

  @Test
  public void matches_invocation_with_custom_logic() {
    given(willReturn(object), new Captor() {
      public boolean matches(Invocation invocation) {
        assume(invocation.method.getReturnType() == Object.class);
        return invocation.instance == mock;
      }
    });
    assertSame(object, mock.get(0));
    assertNotSame(object, otherMock.get(0));
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
  public void captor_cannot_be_null() {
    try {
      given(handler, (Captor) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void handler_can_return_object() {
    class Foo {
      Object method() {
        return null;
      }
    }
    Foo foo = mock(Foo.class);
    given(willReturn(object), foo).method();
    assertEquals(object, foo.method());
  }

  @Test
  public void handler_can_throw_throwable() throws Throwable {
    class Foo {
      Object method() throws Throwable {
        return null;
      }
    }
    Foo foo = mock(Foo.class);
    given(willThrow(throwable), foo).method();
    try {
      foo.method();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void unstubbed_invocation_returns_null_as_object() {
    class Foo {
      Object method() {
        return object;
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.method());
  }

  @Test
  public void unstubbed_invocation_returns_zero_as_integer() {
    class Foo {
      int method() {
        return -1;
      }
    }
    Foo foo = mock(Foo.class);
    assertEquals(0, foo.method());
  }

  private static void assume(boolean assumption) {
    if (!assumption) {
      throw new RuntimeException("wrong assumption");
    }
  }
}
