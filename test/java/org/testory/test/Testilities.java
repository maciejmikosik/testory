package org.testory.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.testory.Closure;

public class Testilities {
  public static Object newObject(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }

    return new Object() {
      public boolean equals(Object obj) {
        return getClass().isInstance(obj) && toString().equals(obj.toString());
      }

      public int hashCode() {
        return name.hashCode();
      }

      public String toString() {
        return name;
      }
    };
  }

  public static Throwable newThrowable(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    return new Throwable() {
      public String toString() {
        return name;
      }
    };
  }

  public static Closure returning(final Object object) {
    return new Closure() {
      public Object invoke() {
        return object;
      }
    };
  }

  public static Closure throwing(final Throwable throwable) {
    if (throwable == null) {
      throw new NullPointerException();
    }
    return new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    };
  }

  // TODO test Testilities.Invoker
  public static class Invoker {
    public Object invoke(Closure closure) throws Throwable {
      return closure.invoke();
    }
  }

  public static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static StackTraceElement here() {
    return new Exception().getStackTrace()[1];
  }

  /* untested */
  public static StackTraceElement nextLine(StackTraceElement element) {
    return new StackTraceElement(element.getClassName(), element.getMethodName(),
        element.getFileName(), element.getLineNumber() + 1);
  }

  public static List<Object> readDeclaredFields(Object instance) {
    List<Object> values = new ArrayList<Object>();
    try {
      for (final Field field : instance.getClass().getDeclaredFields()) {
        if (!field.isSynthetic()) {
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              field.setAccessible(true);
              return null;
            }
          });
          values.add(field.get(instance));
        }
      }
      return values;
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }
}
