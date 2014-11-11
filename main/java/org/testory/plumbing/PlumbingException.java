package org.testory.plumbing;

public class PlumbingException extends RuntimeException {
  public PlumbingException() {}

  public PlumbingException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public PlumbingException(String message, Throwable cause) {
    super(message, cause);
  }

  public PlumbingException(String message) {
    super(message);
  }

  public PlumbingException(Throwable cause) {
    super(cause);
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw new PlumbingException();
    }
  }
}
