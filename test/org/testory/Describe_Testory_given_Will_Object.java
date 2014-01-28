package org.testory;

import static org.junit.Assert.assertEquals;
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

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_given_Will_Object {
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void should_stub_return() {
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
  public void should_stub_throw() throws Throwable {
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
  public void should_return_null_if_not_stubbed() {
    class Foo {
      Object method() {
        return object;
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.method());
  }

  @Test
  public void should_stubbing_survive_chained_when() {
    class Foo {
      Object method() {
        return null;
      }
    }
    Foo mock = mock(Foo.class);
    given(willReturn(object), mock).method();
    when(object).toString();
    assertSame(object, mock.method());
  }
}
