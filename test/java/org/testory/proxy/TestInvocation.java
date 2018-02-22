package org.testory.proxy;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.testing.Fakes.newObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestInvocation {
  private Object instance, otherInstance;
  private Method method, otherMethod;
  private Object argument, otherArgument, argumentA, argumentB;
  private List<?> arguments;
  private Object result;
  private Throwable throwable;
  private Object original;
  private Invocation invocation;
  private Methods methods;
  private int counter;

  @Before
  public void before() throws NoSuchMethodException {
    instance = newObject("instance");
    otherInstance = newObject("otherInstance");
    argument = newObject("argument");
    otherArgument = newObject("otherArgument");
    argumentA = newObject("argumentA");
    argumentB = newObject("argumentB");
    method = Object.class.getDeclaredMethod("equals", Object.class);
    arguments = asList(argument);
    result = newObject("object");
    throwable = new RuntimeException("throwable");
  }

  @Test
  public void constructor_sets_fields() {
    invocation = invocation(method, instance, arguments);
    assertEquals(method, invocation.method);
    assertEquals(arguments, invocation.arguments);
    assertEquals(instance, invocation.instance);
  }

  @Test
  public void allows_no_arguments() {
    method = Methods.withParameters();
    instance = new Methods();
    arguments = Arrays.asList();
    invocation(method, instance, arguments);
  }

  @Test
  public void arguments_are_defensive_copied() {
    method = Methods.withParameters(Object.class);
    instance = new Methods();
    arguments = asList(argumentA);
    original = new ArrayList<>(arguments);
    invocation = invocation(method, instance, arguments);
    ((List<Object>) arguments).set(0, argumentB);
    assertEquals(original, invocation.arguments);
  }

  @Test
  public void arguments_are_unmodifiable() {
    method = Methods.withParameters(Object.class);
    instance = new Methods();
    arguments = Arrays.asList(argumentA);
    invocation = invocation(method, instance, arguments);
    try {
      invocation.arguments.set(0, argumentB);
      fail();
    } catch (UnsupportedOperationException e) {}
  }

  @Test
  public void unwraps_argument() {
    method = Methods.withParameters(int.class);
    instance = new Methods();
    arguments = Arrays.asList(5);
    invocation(method, instance, arguments);
  }

  @Test
  public void unwraps_and_converts_argument() {
    method = Methods.withParameters(long.class);
    instance = new Methods();
    arguments = Arrays.asList(5);
    invocation(method, instance, arguments);
  }

  @Test
  public void argument_is_not_implicitly_converted_to_varargs() {
    method = Methods.withVarargsParameters(Object[].class);
    instance = new Methods();
    arguments = Arrays.asList(arguments);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void arguments_are_not_implicitly_converted_to_varargs() {
    method = Methods.withVarargsParameters(Object[].class);
    instance = new Methods();
    arguments = Arrays.asList(argumentA, argumentB);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void no_arguments_is_not_implicitly_converted_to_varargs() {
    method = Methods.withVarargsParameters(Object[].class);
    instance = new Methods();
    arguments = Arrays.asList();
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void array_argument_is_converted_to_varargs() {
    method = Methods.withVarargsParameters(Object[].class);
    instance = new Methods();
    arguments = Arrays.asList((Object) new Object[0]);
    invocation(method, instance, arguments);
  }

  @Test
  public void instance_cannot_be_outside_of_method_declaring_class_hierarchy() {
    method = Methods.withParameters();
    instance = new Object();
    arguments = Arrays.asList("");
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void instance_can_extend_method_declaring_class() {
    method = Methods.withParameters();
    instance = new Methods.MethodsSubclass();
    arguments = Arrays.asList();
    invocation(method, instance, arguments);
  }

  @Test
  public void invokes_invocation() throws Throwable {
    method = Methods.returningArgument();
    methods = new Methods() {
      Object returningArgument(Object arg) {
        assertEquals(argument, arg);
        return counter++;
      }
    };
    invocation = invocation(method, methods, Arrays.asList(argument));
    invocation.invoke();
    assertEquals(1, counter);
  }

  @Test
  public void invoke_returns_result() throws Throwable {
    method = Methods.returningArgument();
    methods = new Methods() {
      Object returningArgument(Object arg) {
        return result;
      }
    };
    invocation = invocation(method, methods, Arrays.asList(argument));
    assertEquals(result, invocation.invoke());
  }

  @Test
  public void invoke_throws_throwable() throws Throwable {
    method = Methods.returningArgument();
    methods = new Methods() {
      Object returningArgument(Object arg) throws Throwable {
        throw throwable;
      }
    };
    invocation = invocation(method, methods, Arrays.asList(argument));
    try {
      invocation.invoke();
      fail();
    } catch (Exception e) {
      assertEquals(throwable, e);
    }
  }

  @Test
  public void implements_equals() {
    method = Methods.withParameters(Object.class);
    otherMethod = Methods.otherWithParameters(Object.class);
    instance = new Methods() {
      public boolean equals(Object obj) {
        return true;
      }

      public int hashCode() {
        return 0;
      }
    };
    otherInstance = new Methods() {
      public boolean equals(Object obj) {
        return true;
      }

      public int hashCode() {
        return 0;
      }
    };
    arguments = asList(argument);
    invocation = invocation(method, instance, arguments);

    assertTrue(invocation.equals(invocation));
    assertTrue(invocation.equals(invocation(method, instance, arguments)));
    assertTrue(invocation.equals(invocation(method, instance, asList(argument))));
    assertFalse(invocation.equals(invocation(otherMethod, instance, arguments)));
    assertFalse(invocation.equals(invocation(method, otherInstance, arguments)));
    assertFalse(invocation.equals(invocation(method, instance, asList(otherArgument))));
    assertFalse(invocation.equals(new Object()));
    assertFalse(invocation.equals(null));
    assertEquals(invocation.hashCode(), invocation(method, instance, arguments).hashCode());
  }

  @Test
  public void implements_to_string() {
    invocation = invocation(method, instance, arguments);
    assertEquals(
        format("invocation(%s, %s, %s)", method, instance, arguments),
        invocation.toString());
  }

  @Test
  public void checks_illegal_arguments() throws NoSuchMethodException {
    failsInvocation(
        String.class.getDeclaredMethod("valueOf", Object.class),
        instance,
        arguments);
    failsInvocation(null, instance, arguments);
    failsInvocation(method, null, arguments);
    failsInvocation(method, instance, null);
    failsInvocation(
        Methods.withParameters(Object.class),
        new Methods(),
        asList());
    failsInvocation(
        Methods.withParameters(),
        new Methods(),
        asList(argument));
    failsInvocation(
        Methods.withParameters(Object.class, Object.class),
        new Methods(),
        asList(argument, argumentA, argumentB));
    failsInvocation(
        Methods.withParameters(Object.class, Object.class),
        new Methods(),
        asList(argument));
  }

  private static void failsInvocation(Method method, Object instance, List<?> arguments) {
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @SuppressWarnings("unused")
  private static class Methods {
    Object returningArgument(Object arg) throws Throwable {
      return arg;
    }

    public void invoke() {}

    public void invoke(Object o) {}

    public void invokeOther(Object o) {}

    public void invoke(Object a, Object b) {}

    public void invoke(int o) {}

    public void invoke(long o) {}

    public void invoke(Object[] o) {}

    public void varargs(Object... o) {}

    public static Method returningArgument() {
      try {
        return Methods.class.getDeclaredMethod("returningArgument", Object.class);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public static Method withParameters(Class<?>... parameters) {
      try {
        return Methods.class.getDeclaredMethod("invoke", parameters);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public static Method otherWithParameters(Class<?>... parameters) {
      try {
        return Methods.class.getDeclaredMethod("invokeOther", parameters);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public static Method subWithParameters(Class<?>... parameters) {
      try {
        return MethodsSubclass.class.getDeclaredMethod("invoke", parameters);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public static Method withVarargsParameters(Class<?>... parameters) {
      try {
        return Methods.class.getDeclaredMethod("varargs", parameters);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public static class MethodsSubclass extends Methods {}
  }
}
