package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.test.Testilities.newObject;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class describe_Invocations_invoke {
  private Invocation invocation;
  private Method method;
  private Host host;
  private Object object, argument;
  private Throwable throwable;
  private int counter;

  @Before
  public void before() throws NoSuchMethodException {
    object = newObject("object");
    argument = newObject("argument");
    method = Host.class.getDeclaredMethod("method", Object.class);
    throwable = new RuntimeException("throwable");
  }

  @Test
  public void should_invoke_invocation() throws Throwable {
    host = new Host() {
      Object method(Object arg) {
        assertEquals(argument, arg);
        return counter++;
      }
    };
    invocation = invocation(method, host, Arrays.asList(argument));
    invoke(invocation);
    assertEquals(1, counter);
  }

  @Test
  public void should_return_result_of_invocation() throws Throwable {
    host = new Host() {
      Object method(Object arg) {
        return object;
      }
    };
    invocation = invocation(method, host, Arrays.asList(argument));
    assertEquals(object, invoke(invocation));
  }

  @Test
  public void should_throw_throwable_from_invocation() throws Throwable {
    host = new Host() {
      Object method(Object arg) throws Throwable {
        throw throwable;
      }
    };
    invocation = invocation(method, host, Arrays.asList(argument));
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
    } catch (ProxyException e) {}
  }

  private static class Host {
    @SuppressWarnings("unused")
    Object method(Object arg) throws Throwable {
      return null;
    }
  }
}
