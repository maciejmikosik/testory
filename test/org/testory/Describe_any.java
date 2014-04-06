package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_any {
  private Mock mock, otherMock;
  private Object object, otherObject;

  @Before
  public void before() {
    mock = mock(Mock.class);
    otherMock = mock(Mock.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @After
  public void after() {
    when("");
    when("");
  }

  @Test
  public void compiles_non_generic() {
    new Compile<Object>().compile(any(Object.class));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void compiles_raw() {
    new Compile<List>().compile(any(List.class));
  }

  @Test
  public void compiles_wildcard() {
    new Compile<List<?>>().compile(any(List.class));
  }

  @Test
  public void compiles_unchecked() {
    new Compile<List<String>>().compile(any(List.class));
  }

  @Test
  public void matching_accepts_any_argument() {
    given(willReturn(true), mock).method(any(Object.class));
    assertTrue(mock.method(object));
    thenCalled(mock).method(any(Object.class));
  }

  @Test
  public void matching_accepts_matching_argument() {
    given(willReturn(true), mock).method(any(Object.class, same(object)));
    assertTrue(mock.method(object));
    thenCalled(mock).method(any(Object.class, same(object)));
  }

  @Test
  public void matching_rejects_mismatching_argument() {
    given(willReturn(true), mock).method(any(Object.class, same(object)));
    assertFalse(mock.method(otherObject));
    try {
      thenCalled(mock).method(any(Object.class, same(object)));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void matching_rejects_other_instance() {
    given(willReturn(true), mock).method(any(Object.class));
    assertFalse(otherMock.method(object));
    try {
      thenCalled(mock).method(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void matching_rejects_other_method() {
    given(willReturn(true), mock).method(any(Object.class));
    assertFalse(mock.otherMethod(object));
    try {
      thenCalled(mock).method(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void matching_ignores_type() {
    given(willReturn(true), mock).method(any(Interface.class));
    assertTrue(mock.method(object));
    thenCalled(mock).method(any(Interface.class));
  }

  @Test
  public void printing_includes_matcher() {
    try {
      thenCalled(mock).method(any(Object.class, same(object)));
      fail();
    } catch (TestoryAssertionError e) {
      thenEqual(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.method(any(%s, %s))\n", //
          mock, Object.class.getName(), same(object)), //
          e.getMessage());
    }
  }

  @Test
  public void printing_skips_implicit_matcher() {
    try {
      thenCalled(mock).method(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      thenEqual(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.method(any(%s))\n", //
          mock, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void printing_handles_many_parameters() {
    try {
      thenCalled(mock).method(any(List.class, same(object)), any(Set.class));
      fail();
    } catch (TestoryAssertionError e) {
      thenEqual(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.method(any(%s, %s), any(%s))\n", //
          mock, List.class.getName(), same(object), Set.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void recovers_after_misuse() {
    any(Object.class);
    try {
      given(willReturn(true), mock).method(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    given(willReturn(true), mock).method(any(Object.class));
    assertTrue(mock.method(object));
    thenCalled(mock).method(any(Object.class));
  }

  @Test
  public void fails_for_null_type() {
    try {
      any(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void fails_for_null_matcher() {
    try {
      any(Object.class, null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void fails_for_not_matcher() {
    try {
      any(Object.class, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  private static Object same(final Object object) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return object == item;
      }

      public String toString() {
        return "same(" + object + ")";
      }
    };
  }

  public interface Interface {}

  public interface Mock {
    boolean method(Object o);

    boolean otherMethod(Object o);

    boolean method(Object o1, Object o2);
  }

  class Compile<E> {
    void compile(E o) {}
  }
}
