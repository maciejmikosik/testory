package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.a;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class test_any_a {
  private Mockable mock;
  private Object object, otherObject;

  private final boolean booleanA = true, booleanB = false;
  private final char charA = 'a', charB = 'b';
  private final byte byteA = 1, byteB = 2;
  private final short shortA = 1, shortB = 2;
  private final int intA = 1, intB = 2;
  private final long longA = 1, longB = 2;
  private final float floatA = 1, floatB = 2;
  private final double doubleA = 1, doubleB = 2;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void requires_equal_boolean() {
    given(willReturn(true), mock).invoke(a(booleanA));
    assertTrue(mock.invoke(booleanA));
    assertFalse(mock.invoke(booleanB));
  }

  @Test
  public void requires_equal_char() {
    given(willReturn(true), mock).invoke(a(charA));
    assertTrue(mock.invoke(charA));
    assertFalse(mock.invoke(charB));
  }

  @Test
  public void requires_equal_byte() {
    given(willReturn(true), mock).invoke(a(byteA));
    assertTrue(mock.invoke(byteA));
    assertFalse(mock.invoke(byteB));
  }

  @Test
  public void requires_equal_short() {
    given(willReturn(true), mock).invoke(a(shortA));
    assertTrue(mock.invoke(shortA));
    assertFalse(mock.invoke(shortB));
  }

  @Test
  public void requires_equal_int() {
    given(willReturn(true), mock).invoke(a(intA));
    assertTrue(mock.invoke(intA));
    assertFalse(mock.invoke(intB));
  }

  @Test
  public void requires_equal_long() {
    given(willReturn(true), mock).invoke(a(longA));
    assertTrue(mock.invoke(longA));
    assertFalse(mock.invoke(longB));
  }

  @Test
  public void requires_equal_float() {
    given(willReturn(true), mock).invoke(a(floatA));
    assertTrue(mock.invoke(floatA));
    assertFalse(mock.invoke(floatB));
  }

  @Test
  public void requires_equal_double() {
    given(willReturn(true), mock).invoke(a(doubleA));
    assertTrue(mock.invoke(doubleA));
    assertFalse(mock.invoke(doubleB));
  }

  @Test
  public void requires_equal_object() {
    given(willReturn(true), mock).invoke(a(object));
    assertTrue(mock.invoke(object));
    assertFalse(mock.invoke(otherObject));
  }

  @Test
  public void checks_that_value_is_not_null() {
    try {
      a(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void prints_value() {
    try {
      thenCalled(mock).invoke(a(intA));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(".invoke(a(" + intA + "))"));
    }
    try {
      thenCalled(mock).invoke(a(object));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(".invoke(a(" + object + "))"));
    }
  }

  private static abstract class Mockable {
    abstract boolean invoke(boolean value);

    abstract boolean invoke(char value);

    abstract boolean invoke(byte value);

    abstract boolean invoke(short value);

    abstract boolean invoke(int value);

    abstract boolean invoke(long value);

    abstract boolean invoke(float value);

    abstract boolean invoke(double value);

    abstract boolean invoke(Object value);
  }
}
