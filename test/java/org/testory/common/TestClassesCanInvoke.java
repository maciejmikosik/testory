package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.canInvoke;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestClassesCanInvoke {
  @Test
  public void checks_argument_conversion() throws Exception {
    List<Object> arguments = asList(
        new Object(),
        new Object[0],
        "",
        new String[0],
        null,
        Boolean.valueOf(false),
        Character.valueOf('a'),
        Byte.valueOf((byte) 0),
        Short.valueOf((short) 0),
        Integer.valueOf(0),
        Long.valueOf(0),
        Float.valueOf(0),
        Double.valueOf(0),
        new int[0],
        new float[0],
        new Integer[0],
        new Float[0]);
    for (Method method : Methods.withOneParameter()) {
      for (Object argument : arguments) {
        assertCorrectness(method, null, argument);
      }
    }
  }

  @Test
  public void checks_number_of_parameters() {
    for (Method method : Methods.withObjectParameters()) {
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
    class SubClass extends SuperClass {
      void superMethod() {}

      void method() {}
    }
    assertCorrectness(SubClass.class.getDeclaredMethod("method"), new SubClass());
    assertCorrectness(SubClass.class.getDeclaredMethod("method"), new SuperClass());
    assertCorrectness(SubClass.class.getDeclaredMethod("superMethod"), new SubClass());
    assertCorrectness(SubClass.class.getDeclaredMethod("superMethod"), new SuperClass());
    assertCorrectness(SuperClass.class.getDeclaredMethod("superMethod"), new SubClass());
    assertCorrectness(SuperClass.class.getDeclaredMethod("superMethod"), new SuperClass());

  }

  @Test
  public void ignores_instance_if_method_is_static() {
    assertCorrectness(Methods.withParameters(), new Object());
    assertCorrectness(Methods.withParameters(), null);
  }

  @Test
  public void checks_instance_if_method_is_not_static() throws Exception {
    Method method = Methods.getInstanceMethod();
    try {
      method.invoke(null);
      fail();
    } catch (NullPointerException e) {}
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
      canInvoke(Methods.withParameters(Object.class), null, (Object[]) null);
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
    } catch (ReflectiveOperationException e) {
      throw new LinkageError(null, e);
    }
  }

  @SuppressWarnings("unused")
  private static class Methods {
    public static void invoke() {}

    public static void invoke(Object arg) {}

    public static void invoke(Object[] arg) {}

    public static void invoke(String arg) {}

    public static void invoke(String[] arg) {}

    public static void invoke(Object argA, Object argB) {}

    public static void invoke(Object argA, Object argB, Object argC) {}

    public static void invoke(Object arg, Object... varargs) {}

    public static void invoke(boolean arg) {}

    public static void invoke(char arg) {}

    public static void invoke(byte arg) {}

    public static void invoke(short arg) {}

    public static void invoke(int arg) {}

    public static void invoke(long arg) {}

    public static void invoke(float arg) {}

    public static void invoke(double arg) {}

    public static void invoke(Void arg) {}

    public static void invoke(Boolean arg) {}

    public static void invoke(Character arg) {}

    public static void invoke(Byte arg) {}

    public static void invoke(Short arg) {}

    public static void invoke(Integer arg) {}

    public static void invoke(Long arg) {}

    public static void invoke(Float arg) {}

    public static void invoke(Double arg) {}

    public static void invoke(int[] arg) {}

    public static void invoke(float[] arg) {}

    public static void invoke(Integer[] arg) {}

    public static void invoke(Float[] arg) {}

    public void nonStaticMethod() {}

    public static List<Method> withOneParameter() {
      List<Method> methods = new ArrayList<>();
      for (Method method : Methods.class.getDeclaredMethods()) {
        if (method.getName().equals("invoke") && hasOneParameter(method)) {
          methods.add(method);
        }
      }
      assume(methods.size() > 5);
      return methods;
    }

    private static boolean hasOneParameter(Method method) {
      return method.getParameterTypes().length == 1;
    }

    public static List<Method> withObjectParameters() {
      List<Method> methods = new ArrayList<>();
      for (Method method : Methods.class.getDeclaredMethods()) {
        if (method.getName().equals("invoke") && hasObjectParameters(method)) {
          methods.add(method);
        }
      }
      assume(methods.size() > 2);
      return methods;
    }

    private static boolean hasObjectParameters(Method method) {
      for (Class<?> parameter : method.getParameterTypes()) {
        if (parameter != Object.class) {
          return false;
        }
      }
      return true;
    }

    private static Method withParameters(Class<?>... parameters) {
      try {
        return Methods.class.getDeclaredMethod("invoke", parameters);
      } catch (NoSuchMethodException e) {
        throw new Error(e);
      }
    }

    public static Method getInstanceMethod() {
      try {
        return Methods.class.getDeclaredMethod("nonStaticMethod");
      } catch (NoSuchMethodException e) {
        throw new Error(e);
      }
    }

    private static void assume(boolean condition) {
      if (!condition) {
        throw new RuntimeException();
      }
    }
  }
}
