package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.test.Testilities.newObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

// TODO rewrite: do not duplicate what tests of canInvoke already do
public class Describe_Invocation {
  private Object instance, otherInstance;
  private Object argument, argumentA, argumentB;
  private List<?> arguments, original;
  private Method method, otherMethod;
  private Invocation invocation, otherInvocation;

  @Before
  public void before() throws NoSuchMethodException {
    instance = newObject("instance");
    otherInstance = newObject("otherInstance");
    argument = newObject("argument");
    argumentA = newObject("argumentA");
    argumentB = newObject("argumentB");
    method = Object.class.getDeclaredMethod("equals", new Class[] { Object.class });
    original = Arrays.asList();
    arguments = Arrays.asList(argument);
  }

  @Test
  public void should_get_method() {
    invocation = invocation(method, instance, arguments);
    assertEquals(method, invocation.method);
  }

  @Test
  public void should_get_arguments() {
    invocation = invocation(method, instance, arguments);
    assertEquals(arguments, invocation.arguments);
  }

  @Test
  public void should_get_instance() {
    invocation = invocation(method, instance, arguments);
    assertEquals(instance, invocation.instance);
  }

  @Test
  public void should_allow_no_arguments() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo() {}
    }
    method = Foo.class.getDeclaredMethod("foo");
    instance = new Foo();
    arguments = Arrays.asList();
    invocation(method, instance, arguments);
  }

  @Test
  public void should_fail_for_argument_and_method_with_no_parameters() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo() {}
    }
    method = Foo.class.getDeclaredMethod("foo");
    instance = new Foo();
    arguments = Arrays.asList(argument);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_unbox_wrapper() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(int arg) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { int.class });
    instance = new Foo();
    arguments = Arrays.asList(5);
    invocation(method, instance, arguments);
  }

  @Test
  public void should_unbox_and_convert_wrapper() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(long arg) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { long.class });
    instance = new Foo();
    arguments = Arrays.asList(5);
    invocation(method, instance, arguments);
  }

  @Test
  public void should_fail_for_too_many_arguments() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object argA, Object argB) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object.class, Object.class });
    instance = new Foo();
    arguments = Arrays.asList(argument, argumentA, argumentB);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_too_few_arguments() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object argA, Object argB) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object.class, Object.class });
    instance = new Foo();
    arguments = Arrays.asList(arguments);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_argument_and_method_with_varargs() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object... args) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object[].class });
    instance = new Foo();
    arguments = Arrays.asList(arguments);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_many_arguments_and_method_with_varargs() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object... args) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object[].class });
    instance = new Foo();
    arguments = Arrays.asList(argumentA, argumentB);
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_no_arguments_and_method_with_varags() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object... args) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object[].class });
    instance = new Foo();
    arguments = Arrays.asList();
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_allow_array_argument_for_varargs_parameter() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void foo(Object... args) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object[].class });
    instance = new Foo();
    arguments = Arrays.asList((Object) new Object[0]);
    invocation(method, instance, arguments);
  }

  @Test
  public void should_fail_for_method_not_declared_in_instance_type() throws NoSuchMethodException {
    method = String.class.getDeclaredMethod("concat", new Class[] { String.class });
    instance = new Object();
    arguments = Arrays.asList("");
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_allow_method_declared_in_supertype_of_instance_type()
      throws NoSuchMethodException {
    class Superclass {
      @SuppressWarnings("unused")
      void method() {}
    }
    class Subclass extends Superclass {}
    method = Superclass.class.getDeclaredMethod("method");
    instance = new Subclass();
    arguments = Arrays.asList();
    invocation(method, instance, arguments);
  }

  @Test
  public void should_implement_to_string() {
    invocation = invocation(method, instance, arguments);
    assertEquals("invocation(" + method + ", " + instance + ", " + arguments + ")",
        invocation.toString());
  }

  @Test
  public void should_defensive_copy_accepted_arguments() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      public void foo(Object o) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object.class });
    instance = new Foo();
    arguments = Arrays.asList(argumentA);
    original = new ArrayList<Object>(arguments);
    invocation = invocation(method, instance, arguments);
    ((List<Object>) arguments).set(0, argumentB);
    assertEquals(original, invocation.arguments);
  }

  @Test
  public void should_arguments_be_unmodifiable() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      public void foo(Object o) {}
    }
    method = Foo.class.getDeclaredMethod("foo", new Class[] { Object.class });
    instance = new Foo();
    arguments = Arrays.asList(argumentA);
    invocation = invocation(method, instance, arguments);
    try {
      invocation.arguments.set(0, argumentB);
      fail();
    } catch (UnsupportedOperationException e) {}
  }

  @Test
  public void should_fail_for_static_method() throws NoSuchMethodException {
    method = String.class.getDeclaredMethod("valueOf", new Class[] { Object.class });
    try {
      invocation(method, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_be_equal_to_equal_invocation() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, instance, arguments);
    assertEquals(invocation, otherInvocation);
  }

  @Test
  public void should_be_equal_to_equal_invocation_with_equal_array_argument() {
    invocation = invocation(method, instance, Arrays.asList((Object) new Object[0]));
    otherInvocation = invocation(method, instance, Arrays.asList((Object) new Object[0]));
    assertEquals(invocation, otherInvocation);
  }

  @Test
  public void should_not_be_equal_to_invocation_of_not_equal_method() throws NoSuchMethodException {
    class Foo {
      @SuppressWarnings("unused")
      void method(Object arg) {}

      @SuppressWarnings("unused")
      void otherMethod(Object arg) {}
    }
    method = Foo.class.getDeclaredMethod("method", new Class[] { Object.class });
    otherMethod = Foo.class.getDeclaredMethod("otherMethod", new Class[] { Object.class });
    instance = new Foo();
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(otherMethod, instance, arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void should_not_be_equal_to_invocation_on_equal_but_not_same_instance() {
    invocation = invocation(method, new Integer(10), arguments);
    otherInvocation = invocation(method, new Integer(10), arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void should_not_be_equal_to_invocation_on_not_equal_instance() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, otherInstance, arguments);
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void should_not_be_equal_to_invocation_with_not_equal_arguments() {
    invocation = invocation(method, instance, Arrays.asList(argumentA));
    otherInvocation = invocation(method, instance, Arrays.asList(argumentB));
    assertFalse(invocation.equals(otherInvocation));
  }

  @Test
  public void should_be_equal_to_same_invocation() {
    invocation = invocation(method, instance, arguments);
    assertEquals(invocation, invocation);
  }

  @Test
  public void should_not_be_equal_to_not_invocation() {
    invocation = invocation(method, instance, arguments);
    assertFalse(invocation.equals(new Object()));
  }

  @Test
  public void should_not_be_equal_to_null() {
    invocation = invocation(method, instance, arguments);
    assertFalse(invocation.equals(null));
  }

  @Test
  public void should_implement_hashcode() {
    invocation = invocation(method, instance, arguments);
    otherInvocation = invocation(method, instance, arguments);
    assertEquals(invocation.hashCode(), otherInvocation.hashCode());
  }

  @Test
  public void should_fail_for_null_method() {
    try {
      invocation(null, instance, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_null_instance() {
    try {
      invocation(method, null, arguments);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void should_fail_for_null_arguments() {
    try {
      invocation(method, instance, null);
      fail();
    } catch (ProxyException e) {}
  }
}
