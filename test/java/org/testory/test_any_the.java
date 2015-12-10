package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.the;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Matchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class test_any_the {
  private Mockable mock;
  private Object object, equalObject, otherObject;

  private final boolean booleanA = true;
  private final char charA = 'a';
  private final byte byteA = 1;
  private final short shortA = 1;
  private final int intA = 1;
  private final long longA = 1;
  private final float floatA = 1;
  private final double doubleA = 1;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    object = newObject("object");
    equalObject = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void requires_same_instance() {
    given(willReturn(true), mock).invoke(the(object));
    assertTrue(mock.invoke(object));
    assertFalse(mock.invoke(equalObject));
    assertFalse(mock.invoke(otherObject));
  }

  @Test
  public void fails_for_primitives() {
    try {
      the(booleanA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(charA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(byteA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(shortA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(intA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(longA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(floatA);
      fail();
    } catch (TestoryException e) {}
    try {
      the(doubleA);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_value_is_not_null() {
    try {
      the(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void prints_value() {
    try {
      thenCalled(mock).invoke(the(object));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(".invoke(the(" + object + "))"));
    }
  }

  private static abstract class Mockable {
    abstract boolean invoke(Object value);
  }
}
