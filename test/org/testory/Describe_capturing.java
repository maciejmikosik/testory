package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Describe_capturing {
  private Object object, otherObject;
  private int value, otherValue;
  private Mockable mock, otherMock;
  private Handler handler;
  private Object numberMatcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    value = 123;
    otherValue = 456;
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    handler = new Handler() {
      public Object handle(Invocation invocation) {
        return null;
      }
    };
  }

  @Test
  public void stubbing_supports_capturing() {
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
  public void verification_supports_capturing() {
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
  public void verification_exact_times_supports_capturing() {
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
  public void verification_matching_times_supports_capturing() {
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
  public void verification_checks_arguments() {
    try {
      thenCalled((Object) null);
      fail();
    } catch (TestoryException e) {}
    try {
      thenCalledTimes(1, (Object) null);
      fail();
    } catch (TestoryException e) {}
    try {
      thenCalledTimes(numberMatcher, (Object) null);
      fail();
    } catch (TestoryException e) {}
    try {
      thenCalled(new Object());
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(1, new Object());
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(number(2), new Object());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void capturing_works_with_varargs() {
    given(willReturn(true), mock).varargs(object, object);
    assertFalse(mock.varargs(object));
    assertTrue(mock.varargs(object, object));
    assertTrue(mock.varargs(object, new Object[] { object }));
    assertFalse(mock.varargs(object, otherObject));
    assertFalse(mock.varargs(object, object, object));
  }

  @Test
  public void capturing_works_with_primitive_varargs() {
    given(willReturn(true), mock).primitiveVarargs(value, value);
    assertFalse(mock.primitiveVarargs(value));
    assertTrue(mock.primitiveVarargs(value, value));
    assertTrue(mock.primitiveVarargs(value, new int[] { value }));
    assertFalse(mock.primitiveVarargs(value, otherValue));
    assertFalse(mock.primitiveVarargs(value, value, value));
  }

  @Test
  public void captured_invocation_is_printable() {
    try {
      thenCalled(mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(mock + ".invoke()"));
    }
    try {
      thenCalled(mock).returnObject(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(mock + ".returnObject(" + object + ")"));
    }
    try {
      thenCalled(mock).acceptObjects(object, otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(
          mock + ".acceptObjects(" + object + ", " + otherObject + ")"));
    }
    try {
      thenCalled(mock).varargs(object, otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage().contains(mock + ".varargs(" + object + ", [" + otherObject + "])"));
    }
  }

  private static Object number(final Integer... numbers) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return Arrays.asList(numbers).contains(item);
      }

      public String toString() {
        return "number(" + Arrays.toString(numbers) + ")";
      }
    };
  }

  private static class Mockable {
    public void invoke() {}

    public Object returnObject(Object object) {
      return null;
    }

    public Object returnOtherObject(Object object) {
      return null;
    }

    public boolean varargs(Object object, Object... objects) {
      return false;
    }

    public boolean primitiveVarargs(int value, int... values) {
      return false;
    }

    public void acceptObjects(Object object, Object otherObject) {}
  }
}
