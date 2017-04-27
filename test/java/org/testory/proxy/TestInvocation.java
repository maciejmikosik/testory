package org.testory.proxy;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
  private Object argument, argumentA, argumentB;
  private List<?> arguments;
  private Object result;
  private Throwable throwable;
  private Object original;
  private Invocation invocation, otherInvocation;
  private Methods methods;
  private int counter;

  @Before
  public void before() throws NoSuchMethodException {
    instance = newObject("instance");
    otherInstance = newObject("otherInstance");
    argument = newObject("argument");
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
  public void detects_missing_argument() {
    method = Methods.withParameters();
    instance = new Methods();
    arguments = Arrays.asList(argument);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void detects_too_many_arguments() {
    method = Methods.withParameters(Object.class, Object.class);
    instance = new Methods();
    arguments = Arrays.asList(argument, argumentA, argumentB);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void detects_too_few_arguments() {
    method = Methods.withParameters(Object.class, Object.class);
    instance = new Methods();
    arguments = Arrays.asList(arguments);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
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
  public void is_equal_to_itself() {
    invocation = invocation(method, instance, arguments);
    assertEquals(invocation, invocation);
  }

  @Test
  public void is_equal_if_all_fields_are_same() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, instance, arguments);
    assertEquals(invocation, otherInvocation);
  }

  @Test
  public void is_equal_if_arguments_are_equal() {
    invocation = invocation(method, instance, Arrays.asList((Object) new Object[0]));
    otherInvocation = invocation(method, instance, Arrays.asList((Object) new Object[0]));
    assertEquals(invocation, otherInvocation);
  }

  @Test
  public void is_not_equal_if_method_is_not_equal() {
    method = Methods.withParameters(Object.class);
    otherMethod = Methods.otherWithParameters(Object.class);
    instance = new Methods();
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(otherMethod, instance, arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void is_not_equal_if_instance_is_not_same() {
    invocation = invocation(method, new Integer(10), arguments);
    otherInvocation = invocation(method, new Integer(10), arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void is_not_equal_if_instance_is_not_equal() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, otherInstance, arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void is_not_equal_if_arguments_are_not_equal() {
    invocation = invocation(method, instance, Arrays.asList(argumentA));
    otherInvocation = invocation(method, instance, Arrays.asList(argumentB));
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void is_not_equal_to_object() {
    invocation = invocation(method, instance, arguments);
    assertFalse(invocation.equals(new Object()));
  }

  @Test
  public void is_not_equal_to_null() {
    invocation = invocation(method, instance, arguments);
    assertFalse(invocation.equals(null));
  }

  @Test
  public void implements_hashcode() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, instance, arguments);
    assertEquals(invocation.hashCode(), otherInvocation.hashCode());
  }

  @Test
  public void implements_to_string() {
    invocation = invocation(method, instance, arguments);
    assertEquals("invocation(" + method + ", " + instance + ", " + arguments + ")",
        invocation.toString());
  }

  @Test
  public void method_cannot_be_static() throws NoSuchMethodException {
    method = String.class.getDeclaredMethod("valueOf", Object.class);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void method_cannot_be_null() {
    try {
      invocation(null, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void instance_cannot_be_null() {
    try {
      invocation(method, null, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void arguments_cannot_be_null_list() {
    try {
      invocation(method, instance, null);
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
