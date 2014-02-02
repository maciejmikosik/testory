package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_capturing {
  private List<Object> mock, otherMock;
  private Object object, otherObject;

  @Before
  public void before() {
    mock = mock(List.class);
    otherMock = mock(List.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Before
  @After
  public void purge_to_isolate_tests() {
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
  public void matches_any_argument() {
    given(willReturn(true), mock).add(any(Object.class));
    assertTrue(mock.add(object));
    thenCalled(mock).add(any(Object.class));
  }

  @Test
  public void matches_any_two_argument() {
    given(willReturn(object), mock).set(any(Integer.class), any(Object.class));
    assertSame(object, mock.set(5, otherObject));
    thenCalled(mock).set(any(Integer.class), any(Object.class));
  }

  @Test
  public void matches_null_argument() {
    given(willReturn(true), mock).add(any(Object.class));
    assertTrue(mock.add(null));
    thenCalled(mock).add(any(Object.class));
  }

  @Test
  public void not_matches_any_instance() {
    given(willReturn(true), mock).add(any(Object.class));
    assertFalse(otherMock.add(object));
    try {
      thenCalled(mock).add(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".add(any(" + Object.class.getName() + "))" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void not_matches_any_method() {
    given(willReturn(true), mock).add(any(Object.class));
    assertFalse(mock.contains(object));
    try {
      thenCalled(mock).add(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".add(any(" + Object.class.getName() + "))" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void ignores_captor_type() {
    given(willReturn(true), mock).add(any(String.class));
    assertTrue(mock.add(object));
    thenCalled(mock).add(any(String.class));
  }

  @Test
  public void cannot_use_more_captors_than_parameters() {
    try {
      any(Object.class);
      given(willReturn(true), mock).add(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    try {
      any(Object.class);
      thenCalled(mock).add(any(Object.class));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void recovers_after_misuse() {
    try {
      any(Object.class);
      given(willReturn(true), mock).add(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    given(willReturn(true), mock).add(any(Object.class));
    assertTrue(mock.add(object));
    thenCalled(mock).add(any(Object.class));
  }

  @Test
  public void cannot_mix_captors_and_arguments() {
    try {
      given(willReturn(true), mock).set(1, any(Object.class));
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalled(mock).set(1, any(Object.class));
      fail();
    } catch (TestoryException e) {}
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
}
