package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.canInvoke;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_Classes_canInvoke {
  private List<Object> arguments;
  private List<Method> methods;

  @Before
  public void before() {
    arguments = new ArrayList<Object>();
    arguments.add(new Object());
    arguments.add(new Object[0]);
    arguments.add("");
    arguments.add(new String[0]);
    arguments.add(null);

    arguments.add(Boolean.valueOf(false));
    arguments.add(Character.valueOf('a'));
    arguments.add(Byte.valueOf((byte) 0));
    arguments.add(Short.valueOf((short) 0));
    arguments.add(Integer.valueOf(0));
    arguments.add(Long.valueOf(0));
    arguments.add(Float.valueOf(0));
    arguments.add(Double.valueOf(0));

    arguments.add(new int[0]);
    arguments.add(new float[0]);
    arguments.add(new Integer[0]);
    arguments.add(new Float[0]);

    methods = new ArrayList<Method>();
  }

  @Test
  public void checks_argument_conversion() throws Exception {
    for (Method method : Methods.class.getDeclaredMethods()) {
      if (method.getName().equals("method") && method.getParameterTypes().length == 1) {
        methods.add(method);
      }
    }
    assume(methods.size() > 5);

    for (Method method : methods) {
      for (Object argument : arguments) {
        assertCorrectness(method, null, argument);
      }
    }
  }

  @Test
  public void checks_number_of_parameters() {
    methods.add(staticMethod());
    methods.add(staticMethod(Object.class));
    methods.add(staticMethod(Object.class, Object.class));
    methods.add(staticMethod(Object.class, Object.class, Object.class));
    for (Method method : methods) {
      for (int i = 0; i <= 4; i++) {
        assertCorrectness(method, null, new Object[i]);
      }
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void checks_instance_assignability() throws Exception {
    class SuperClass {
      void superMethod() {}
    }
    class MyClass extends SuperClass {
      void superMethod() {}

      void method() {}
    }
    assertCorrectness(MyClass.class.getDeclaredMethod("method"), new MyClass());
    assertCorrectness(MyClass.class.getDeclaredMethod("method"), new SuperClass());
    assertCorrectness(MyClass.class.getDeclaredMethod("superMethod"), new MyClass());
    assertCorrectness(MyClass.class.getDeclaredMethod("superMethod"), new SuperClass());
    assertCorrectness(SuperClass.class.getDeclaredMethod("superMethod"), new MyClass());
    assertCorrectness(SuperClass.class.getDeclaredMethod("superMethod"), new SuperClass());

  }

  @Test
  public void checks_instance_of_static_method() {
    assertCorrectness(staticMethod(), new Object());
    assertCorrectness(staticMethod(), null);
  }

  @Test
  public void instance_cannot_be_null_for_non_static_method() throws Exception {
    Method method = method("nonStaticMethod");
    try {
      method.invoke(null);
      fail();
    } catch (NullPointerException e) {
      // learning that native implementation behaves differently
    }
    assertFalse(canInvoke(method, null));
  }

  @Test
  public void method_cannot_be_null() {
    try {
      canInvoke(null, null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void arguments_cannot_be_null_array() {
    try {
      canInvoke(staticMethod(Object.class), null, (Object[]) null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static void assertCorrectness(Method method, Object instance, Object... arguments) {
    String message = "canInvoke(" + method + ", " + instance + ", " + asList(arguments) + ")";
    boolean expected = canJavaInvoke(method, instance, arguments);
    boolean actual = canInvoke(method, instance, arguments);
    assertEquals(message, expected, actual);
  }

  private static boolean canJavaInvoke(Method method, Object instance, Object... arguments) {
    try {
      method.invoke(instance, arguments);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void assume(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }

  private static Method staticMethod(Class<?>... parameters) {
    return method("method", parameters);
  }

  private static Method method(String name, Class<?>... parameters) {
    try {
      return Methods.class.getDeclaredMethod(name, parameters);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unused")
  private static class Methods {
    // @formatter:off
    public static void method() {}
    public static void method(Object arg) {}
    public static void method(Object[] arg) {}
    public static void method(String arg) {}
    public static void method(String[] arg) {}
    public static void method(Object argA, Object argB) {}
    public static void method(Object argA, Object argB, Object argC) {}
    public static void method(Object arg, Object... varargs) {}
    public static void method(boolean arg) {}
    public static void method(char arg) {}
    public static void method(byte arg) {}
    public static void method(short arg) {}
    public static void method(int arg) {}
    public static void method(long arg) {}
    public static void method(float arg) {}
    public static void method(double arg) {}
    public static void method(Void arg) {}
    public static void method(Boolean arg) {}
    public static void method(Character arg) {}
    public static void method(Byte arg) {}
    public static void method(Short arg) {}
    public static void method(Integer arg) {}
    public static void method(Long arg) {}
    public static void method(Float arg) {}
    public static void method(Double arg) {}
    public static void method(int[] arg) {}
    public static void method(float[] arg) {}
    public static void method(Integer[] arg) {}
    public static void method(Float[] arg) {}

    public void nonStaticMethod() {}
    // @formatter:on
  }
}
