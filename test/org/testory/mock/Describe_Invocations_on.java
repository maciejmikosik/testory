package org.testory.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.mock.Invocation.invocation;
import static org.testory.mock.Invocations.on;
import static org.testory.test.TestUtils.newObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_Invocations_on {
  private Invocation invocation, invocationOnInstance;
  private Method method;
  private Object instance, argument;
  private List<?> arguments;

  @Before
  public void before() throws NoSuchMethodException {
    instance = newObject("instance");
    argument = newObject("argument");
    method = Object.class.getDeclaredMethod("equals", Object.class);
    arguments = Arrays.asList(argument);
    invocation = invocation(method, newObject("originalInstance"), arguments);
  }

  @Test
  public void should_have_same_method() {
    invocationOnInstance = on(instance, invocation);
    assertEquals(method, invocationOnInstance.method);
  }

  @Test
  public void should_have_different_instance() {
    invocationOnInstance = on(instance, invocation);
    assertEquals(instance, invocationOnInstance.instance);
  }

  @Test
  public void should_have_same_arguments() {
    invocationOnInstance = on(instance, invocation);
    assertEquals(arguments, invocationOnInstance.arguments);
  }

  @Test
  public void should_fail_for_null_instance() {
    try {
      on(null, invocation);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void should_fail_for_null_invocation() {
    try {
      on(instance, null);
      fail();
    } catch (NullPointerException e) {}
  }
}
