package org.testory.testing;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class Reflections {
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
