package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

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
    checkNotNull(throwable);
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

  public static LinkageError newLinkageError(Throwable cause) {
    checkNotNull(cause);
    return new LinkageError(null, cause);
  }
}
