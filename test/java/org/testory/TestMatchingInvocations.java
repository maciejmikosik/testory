package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.willReturn;
import static org.testory.proxy.handler.ReturningHandler.returning;
import static org.testory.testing.DynamicMatchers.number;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;

public class TestMatchingInvocations {
  private Object object, otherObject;
  private int value, otherValue;
  private Mockable mock, otherMock;
  private Handler handler;
  private Object numberMatcher;

  @Before
  public void before() {
    triggerPurge();
    object = newObject("object");
    otherObject = newObject("otherObject");
    value = 123;
    otherValue = 456;
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    handler = returning(null);
  }

  @Test
  public void is_supported_by_stubbing() {
    given(willReturn(object), mock).returnObject(object);
    assertSame(object, mock.returnObject(object));
    assertNotSame(object, otherMock.returnObject(object));
    assertNotSame(object, mock.returnOtherObject(object));
    assertNotSame(object, mock.returnObject(otherObject));
  }

  @Test
  public void stubbing_checks_arguments() {
    try {
      given(null, mock);
      fail();
    } catch (TestoryException e) {}
    try {
      given(handler, (Object) null);
      fail();
    } catch (TestoryException e) {}
    try {
      given(handler, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void is_supported_by_verification_supports() {
    mock.returnObject(object);
    thenCalled(mock).returnObject(object);
    try {
      thenCalled(otherMock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalled(mock).returnOtherObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalled(mock).returnObject(otherObject);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void is_supported_by_verification_exact_times() {
    mock.returnObject(object);
    mock.returnObject(object);
    thenCalledTimes(2, mock).returnObject(object);
    try {
      thenCalledTimes(2, otherMock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(2, mock).returnOtherObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(2, mock).returnObject(otherObject);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(3, mock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void is_supported_by_verification_in_order() {
    mock.returnObject(object);
    thenCalledInOrder(mock).returnObject(object);

    mock.returnObject(object);
    try {
      thenCalledInOrder(mock).returnObject(otherObject);
      fail();
    } catch (TestoryAssertionError e) {}

    mock.returnObject(object);
    try {
      thenCalledInOrder(mock).returnOtherObject(object);
      fail();
    } catch (TestoryAssertionError e) {}

    mock.returnObject(object);
    try {
      thenCalledInOrder(otherMock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void is_supported_by_verification_matching_times() {
    numberMatcher = number(2);
    mock.returnObject(object);
    mock.returnObject(object);
    thenCalledTimes(numberMatcher, mock).returnObject(object);
    try {
      thenCalledTimes(numberMatcher, otherMock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(numberMatcher, mock).returnOtherObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(numberMatcher, mock).returnObject(otherObject);
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(number(3), mock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void works_with_varargs() {
    given(willReturn(true), mock).varargs(object, object);
    assertFalse(mock.varargs(object));
    assertTrue(mock.varargs(object, object));
    assertTrue(mock.varargs(object, new Object[] { object }));
    assertFalse(mock.varargs(object, otherObject));
    assertFalse(mock.varargs(object, object, object));
  }

  @Test
  public void works_with_primitive_varargs() {
    given(willReturn(true), mock).primitiveVarargs(value, value);
    assertFalse(mock.primitiveVarargs(value));
    assertTrue(mock.primitiveVarargs(value, value));
    assertTrue(mock.primitiveVarargs(value, new int[] { value }));
    assertFalse(mock.primitiveVarargs(value, otherValue));
    assertFalse(mock.primitiveVarargs(value, value, value));
  }

  @Test
  public void matched_invocation_is_printable() {
    try {
      thenCalled(mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.invoke()", mock)));
    }
    try {
      thenCalled(mock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.returnObject(%s)", mock, object)));
    }
    try {
      thenCalled(mock).acceptObjects(object, otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.acceptObjects(%s, %s)", mock, object, otherObject)));
    }
    try {
      thenCalled(mock).varargs(object, otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("%s.varargs(%s, [%s])", mock, object, otherObject)));
    }
  }

  private static abstract class Mockable {
    abstract void invoke();

    abstract Object returnObject(Object object);

    abstract Object returnOtherObject(Object object);

    abstract boolean varargs(Object object, Object... objects);

    abstract boolean primitiveVarargs(int value, int... values);

    abstract void acceptObjects(Object object, Object otherObject);
  }
}
