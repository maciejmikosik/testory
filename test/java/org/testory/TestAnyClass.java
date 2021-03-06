package org.testory;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;
import static org.testory.testing.DynamicMatchers.same;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.Purging.triggerPurge;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAnyClass {
  private Mockable mock, otherMock;
  private Object object, otherObject;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @After
  public void after() {
    triggerPurge();
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void compiles_with_various_types() {
    new Compile<Object>().compile(any(Object.class));
    new Compile<List>().compile(any(List.class));
    new Compile<List<?>>().compile(any(List.class));
    new Compile<List<String>>().compile(any(List.class));
    new Compile<Iterable>().compile(any(List.class));
    new Compile<Iterable<?>>().compile(any(List.class));
    new Compile<Iterable<String>>().compile(any(List.class));
  }

  @Test
  public void runs_with_various_types() {
    for (Class<?> type : asList(
        Object.class,
        List.class,
        AbstractList.class,
        ArrayList.class,
        Integer.class,
        String.class)) {
      assertThat(any(type), instanceOf(type));
    }
  }

  @Test
  public void stubbing_supports_wildcards() {
    given(willReturn(object), mock).returnObject(any(Object.class));
    assertSame(object, mock.returnObject(object));
    assertSame(object, mock.returnObject(otherObject));
    assertNotSame(object, otherMock.returnObject(object));
    assertNotSame(object, mock.returnOtherObject(object));
  }

  @Test
  public void verification_supports_wildcards() {
    mock.returnObject(object);
    thenCalled(mock).returnObject(any(Object.class));
    try {
      thenCalled(otherMock).returnObject(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalled(mock).returnOtherObject(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void matching_ignores_type() {
    given(willReturn(true), mock).returnBoolean(any(String.class));
    assertTrue(mock.returnBoolean(object));
    thenCalled(mock).returnBoolean(any(String.class));
  }

  @Test
  public void printing_includes_matcher() {
    Object matcher = same(object);
    try {
      thenCalled(mock).returnObject(any(Object.class, matcher));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.returnObject(any(%s, %s))", mock, Object.class.getName(), matcher)));
    }
  }

  @Test
  public void printing_skips_implicit_matcher() {
    try {
      thenCalled(mock).returnObject(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.returnObject(any(%s))", mock, Object.class.getName())));
    }
  }

  @Test
  public void printing_handles_varargs() {
    try {
      thenCalled(mock).varargs(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.varargs(%s, [any(%s)])", mock, object, Object.class.getName())));
    }
  }

  @Test
  public void recovers_after_misuse() {
    any(Object.class);
    try {
      given(willReturn(true), mock).returnBoolean(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    given(willReturn(true), mock).returnBoolean(any(Object.class));
    assertTrue(mock.returnBoolean(object));
    thenCalled(mock).returnBoolean(any(Object.class));
  }

  @Test
  public void any_checks_arguments() {
    try {
      any(null);
      fail();
    } catch (TestoryException e) {}
    try {
      any(null, new Object());
      fail();
    } catch (TestoryException e) {}
    try {
      any(Object.class, null);
      fail();
    } catch (TestoryException e) {}
    try {
      any(Object.class, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  private static abstract class Mockable {
    abstract Object returnObject(Object object);

    abstract Object returnOtherObject(Object object);

    abstract boolean returnBoolean(Object object);

    abstract Object varargs(Object object, Object... objects);
  }

  private static class Compile<E> {
    void compile(E o) {}
  }
}
