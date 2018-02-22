package org.testory.common;

import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
  /**
   * usage
   *
   * <pre>
   * throw gently(anyThrowable);
   * </pre>
   */
  public static RuntimeException gently(Throwable throwable) {
    requireNonNull(throwable);
    if (throwable instanceof RuntimeException) {
      throw (RuntimeException) throwable;
    } else if (throwable instanceof Error) {
      throw (Error) throwable;
    } else {
      throw new RuntimeException("gently", throwable);
    }
  }

  public static String printStackTrace(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    stringWriter.append('\n');
    return stringWriter.toString();
  }
}
