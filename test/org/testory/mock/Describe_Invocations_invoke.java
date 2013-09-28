package org.testory.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testory.mock.Invocation.invocation;
import static org.testory.mock.Invocations.invoke;
import static org.testory.test.TestUtils.newObject;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class Describe_Invocations_invoke {
  private Invocation invocation;
  private Method method;
  private Host host;
  private Object object, argument;
  private Throwable throwable;

  @Before
  public void before() throws NoSuchMethodException {
    object = newObject("object");
    argument = newObject("argument");
    host = mock(Host.class);
    method = Host.class.getDeclaredMethod("method", Object.class);
    throwable = new RuntimeException("throwable");
  }

  @Test
  public void should_invoke_invocation() throws Throwable {
    invocation = invocation(method, host, Arrays.asList(argument));
    invoke(invocation);
    verify(host).method(argument);
  }

  @Test
  public void should_return_result_of_invocation() throws Throwable {
    invocation = invocation(method, host, Arrays.asList(argument));
    given(host.method(argument)).willReturn(object);
    assertEquals(object, invoke(invocation));
  }

  @Test
  public void should_throw_throwable_from_invocation() throws Throwable {
    invocation = invocation(method, host, Arrays.asList(argument));
    given(host.method(argument)).willThrow(throwable);
    try {
      invoke(invocation);
      fail();
    } catch (Exception e) {
      assertEquals(throwable, e);
    }
  }

  @Test
  public void should_fail_for_null_invocation() throws Throwable {
    try {
      invoke(null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static class Host {
    Object method(Object arg) {
      return null;
    }
  }
}
