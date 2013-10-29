package org.testory.test;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class Testilities {
  public static Object newObject(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    return new Object() {
      public String toString() {
        return name;
      }
    };
  }

  @SuppressWarnings("serial")
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
    } catch (IllegalAccessException e) {
      throw new Error(e);
    }
  }
}
