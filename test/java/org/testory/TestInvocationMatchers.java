package org.testory;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.onRequest;
import static org.testory.Testory.onReturn;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.hasMessage;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.InvocationMatcher;

public class TestInvocationMatchers {
  private Mockable mock, otherMock;
  private InvocationMatcher invocationMatcher;
  private Object argument, otherArgument;
  private Method method, otherMethod, otherTypeMethod;
  private List<Object> arguments, otherArguments;
  private Class<?> type;

  @Before
  public void before() throws NoSuchMethodException {
    type = Returnable.class;
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    argument = newObject("argument");
    otherArgument = newObject("otherArgument");
    arguments = asList(argument);
    otherArguments = asList(otherArgument);
    method = Mockable.class.getDeclaredMethod("returnReturnable", Object.class);
    otherMethod = Mockable.class.getDeclaredMethod("returnOtherReturnable", Object.class);
    otherTypeMethod = Mockable.class.getDeclaredMethod("returnObject", Object.class);
  }

  @Test
  public void on_instance_matches_invocation_on_same_instance() {
    invocationMatcher = onInstance(mock);
    assertTrue(invocationMatcher.matches(invocation(method, mock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(otherMethod, mock, arguments)));
    assertFalse(invocationMatcher.matches(invocation(method, otherMock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(method, mock, otherArguments)));
  }

  @Test
  public void on_return_matches_invocation_of_method_returning_same_type() {
    invocationMatcher = onReturn(type);
    assertTrue(invocationMatcher.matches(invocation(method, mock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(otherMethod, mock, arguments)));
    assertFalse(invocationMatcher.matches(invocation(otherTypeMethod, mock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(method, otherMock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(method, mock, otherArguments)));
  }

  @Test
  public void on_request_matches_invocation_of_method_returning_same_type_and_equal_arguments() {
    invocationMatcher = onRequest(type, arguments.toArray());
    assertTrue(invocationMatcher.matches(invocation(method, mock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(otherMethod, mock, arguments)));
    assertFalse(invocationMatcher.matches(invocation(otherTypeMethod, mock, arguments)));
    assertTrue(invocationMatcher.matches(invocation(method, otherMock, arguments)));
    assertFalse(invocationMatcher.matches(invocation(method, mock, otherArguments)));
  }

  @Test
  public void invocation_matchers_are_printable() {
    assertEquals(format("onInstance(%s)", mock), onInstance(mock).toString());
    assertEquals(format("onReturn(%s)", type.getName()), onReturn(type).toString());
    assertEquals(format("onRequest(%s, %s)", type.getName(), argument),
        onRequest(type, argument).toString());
    assertEquals(format("onRequest(%s)", type.getName()), onRequest(type).toString());
  }

  @Test
  public void invocation_matchers_checks_arguments() {
    try {
      onInstance(null);
      fail();
    } catch (TestoryException e) {}
    try {
      onInstance(new Object());
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected mock"));
    }
    try {
      onReturn(null);
      fail();
    } catch (TestoryException e) {}
    try {
      onRequest(null);
      fail();
    } catch (TestoryException e) {}
    try {
      onRequest(type, (Object[]) null);
      fail();
    } catch (TestoryException e) {}
  }

  private static class Returnable {}

  private static abstract class Mockable {
    abstract Returnable returnReturnable(Object o);

    abstract Returnable returnOtherReturnable(Object o);

    abstract Object returnObject(Object o);
  }
}
