package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_any {
  private Foo mock, otherMock;
  private Object object, otherObject;

  public interface Foo {
    boolean foo_Object(Object a);

    boolean bar_Object(Object a);

    boolean foo_Object_Object(Object a, Object b);

    boolean foo_int(int a);

    boolean foo_int_Object_int(int a, Object b, int c);
  }

  @Before
  public void before() {
    purge();

    mock = mock(Foo.class);
    otherMock = mock(Foo.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @After
  public void after() {
    purge();
  }

  private void purge() {
    when("");
    when("");
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void compiles_with_various_types() {
    class Eater<E> {
      void eat(E food) {}

      void eatBool(boolean food) {}

      void eatChar(char food) {}

      void eatByte(byte food) {}

      void eatShort(short food) {}

      void eatInt(int food) {}

      void eatLong(long food) {}

      void eatFloat(float food) {}

      void eatDouble(double food) {}
    }
    new Eater<Object>().eat(any(Object.class));
    new Eater<String>().eat(any(String.class));
    new Eater<Runnable>().eat(any(Runnable.class));
    new Eater<List>().eat(any(List.class));
    new Eater<List<?>>().eat(any(List.class));
    new Eater<List<Object>>().eat(any(List.class));
    new Eater<List<String>>().eat(any(List.class));

    new Eater<Void>().eat(any(Void.class));
    new Eater<Boolean>().eat(any(Boolean.class));
    new Eater<Character>().eat(any(Character.class));
    new Eater<Byte>().eat(any(Byte.class));
    new Eater<Short>().eat(any(Short.class));
    new Eater<Integer>().eat(any(Integer.class));
    new Eater<Long>().eat(any(Long.class));
    new Eater<Float>().eat(any(Float.class));
    new Eater<Double>().eat(any(Double.class));

    new Eater().eatBool(any(Boolean.class));
    new Eater().eatChar(any(Character.class));
    new Eater().eatByte(any(Byte.class));
    new Eater().eatShort(any(Short.class));
    new Eater().eatInt(any(Integer.class));
    new Eater().eatLong(any(Long.class));
    new Eater().eatFloat(any(Float.class));
    new Eater().eatDouble(any(Double.class));
  }

  @Test
  public void matches_other_argument() {
    given(willReturn(true), mock).foo_Object(any(Object.class));
    assertTrue(mock.foo_Object(object));
    thenCalled(mock).foo_Object(any(Object.class));
  }

  @Test
  public void matches_null_argument() {
    given(willReturn(true), mock).foo_Object(any(Object.class));
    assertTrue(mock.foo_Object(null));
    thenCalled(mock).foo_Object(any(Object.class));
  }

  @Test
  public void matches_other_int() {
    given(willReturn(true), mock).foo_int(any(Integer.class));
    assertTrue(mock.foo_int(5));
    thenCalled(mock).foo_int(any(Integer.class));
  }

  @Test
  public void matches_other_two_arguments() {
    given(willReturn(true), mock).foo_Object_Object(any(Object.class), any(Object.class));
    assertTrue(mock.foo_Object_Object(object, otherObject));
    thenCalled(mock).foo_Object_Object(any(Object.class), any(Object.class));
  }

  @Test
  public void not_matches_other_instance() {
    given(willReturn(true), mock).foo_Object(any(Object.class));
    assertFalse(otherMock.foo_Object(object));
    try {
      thenCalled(mock).foo_Object(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object(any(%s))\n", //
          mock, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void not_matches_other_method() {
    given(willReturn(true), mock).foo_Object(any(Object.class));
    assertFalse(mock.bar_Object(object));
    try {
      thenCalled(mock).foo_Object(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object(any(%s))\n", //
          mock, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void ignores_type() {
    given(willReturn(true), mock).foo_Object(any(Foo.class));
    assertTrue(mock.foo_Object(object));
    thenCalled(mock).foo_Object(any(Foo.class));
  }

  @Test
  public void cannot_use_more_anys_than_parameters() {
    try {
      any(Object.class);
      given(willReturn(true), mock).foo_Object(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    try {
      any(Object.class);
      thenCalled(mock).foo_Object(any(Object.class));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void solves_mixing_proxiable_types() {
    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertTrue(mock.foo_Object_Object(object, object));
    thenCalled(mock).foo_Object_Object(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertTrue(mock.foo_Object_Object(object, otherObject));
    thenCalled(mock).foo_Object_Object(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertTrue(mock.foo_Object_Object(object, null));
    thenCalled(mock).foo_Object_Object(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertFalse(mock.foo_Object_Object(otherObject, object));
    try {
      thenCalled(mock).foo_Object_Object(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object_Object(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertFalse(mock.foo_Object_Object(null, object));
    try {
      thenCalled(mock).foo_Object_Object(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object_Object(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_Object_Object(object, any(Object.class));
    assertFalse(mock.foo_Object_Object(null, null));
    try {
      thenCalled(mock).foo_Object_Object(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object_Object(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void solves_mixing_ints_separated_by_any() {
    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_int_Object_int(any(Integer.class), any(Object.class), 0);
    assertTrue(mock.foo_int_Object_int(-1, object, 0));
    thenCalled(mock).foo_int_Object_int(any(Integer.class), any(Object.class), 0);

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo_int_Object_int(any(Integer.class), any(Object.class), 0);
    assertFalse(mock.foo_int_Object_int(0, object, -1));
    try {
      thenCalled(mock).foo_int_Object_int(any(Integer.class), any(Object.class), 0);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_int_Object_int(any(%s), any(%s), %s)\n", //
          mock, Integer.class.getName(), Object.class.getName(), 0), //
          e.getMessage());
    }
  }

  @Test
  public void recovers_after_misuse() {
    try {
      any(Object.class);
      given(willReturn(true), mock).foo_Object(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    given(willReturn(true), mock).foo_Object(any(Object.class));
    assertTrue(mock.foo_Object(object));
    thenCalled(mock).foo_Object(any(Object.class));
  }

  @Test
  public void matches_matching_argument() {
    given(willReturn(true), mock).foo_Object(any(Object.class, same(object)));
    assertTrue(mock.foo_Object(object));
    thenCalled(mock).foo_Object(any(Object.class, same(object)));
  }

  @Test
  public void not_matches_mismatching_argument() {
    given(willReturn(true), mock).foo_Object(any(Object.class, same(object)));
    assertFalse(mock.foo_Object(otherObject));

    try {
      thenCalled(mock).foo_Object(any(Object.class, same(object)));
      fail();
    } catch (TestoryAssertionError e) {
      thenEqual(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo_Object(any(%s, %s))\n", //
          mock, Object.class.getName(), same(object)), //
          e.getMessage());
    }
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

  @Test
  public void type_cannot_be_null() {
    try {
      any(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void type_cannot_be_primitive() {
    try {
      any(int.class);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_any_object() {
    try {
      any(Object.class, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      any(Object.class, null);
      fail();
    } catch (TestoryException e) {}
  }
}
