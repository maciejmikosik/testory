package org.testory;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.anyInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.Purging.triggerPurge;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAnyInstanceOf {
  private Mockable mock;
  private Object object;
  private String string;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    object = newObject("object");
    string = "string";
  }

  @After
  public void after() {
    triggerPurge();
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void compiles_with_various_types() {
    new Compile<Object>().compile(anyInstanceOf(Object.class));
    new Compile<List>().compile(anyInstanceOf(List.class));
    new Compile<List<?>>().compile(anyInstanceOf(List.class));
    new Compile<List<String>>().compile(anyInstanceOf(List.class));
    new Compile<Iterable>().compile(anyInstanceOf(List.class));
    new Compile<Iterable<?>>().compile(anyInstanceOf(List.class));
    new Compile<Iterable<String>>().compile(anyInstanceOf(List.class));
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
      assertThat(anyInstanceOf(type), instanceOf(type));
    }
  }

  @Test
  public void matching_rejects_supertype() {
    given(willReturn(true), mock).invoke(anyInstanceOf(String.class));
    assertFalse(mock.invoke(object));
    thenCalledNever(mock).invoke(anyInstanceOf(String.class));
  }

  @Test
  public void matching_accepts_same_type() {
    given(willReturn(true), mock).invoke(anyInstanceOf(String.class));
    assertTrue(mock.invoke(string));
    thenCalled(mock).invoke(anyInstanceOf(String.class));
  }

  @Test
  public void matching_accepts_subtype() {
    given(willReturn(true), mock).invoke(anyInstanceOf(Object.class));
    assertTrue(mock.invoke(string));
    thenCalled(mock).invoke(anyInstanceOf(Object.class));
  }

  @Test
  public void is_printable() {
    try {
      thenCalled(mock).invoke(anyInstanceOf(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.invoke(anyInstanceOf(%s))", mock, Object.class.getName())));
    }
  }

  @Test
  public void type_cannot_be_null() {
    try {
      anyInstanceOf(null);
      fail();
    } catch (TestoryException e) {}
  }

  private static abstract class Mockable {
    abstract boolean invoke(Object object);
  }

  private static class Compile<E> {
    void compile(E o) {}
  }
}
