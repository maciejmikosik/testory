package org.testory.testing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class Testilities {

  public static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static StackTraceElement here() {
    return new Exception().getStackTrace()[1];
  }

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
