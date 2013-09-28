package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

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
}
