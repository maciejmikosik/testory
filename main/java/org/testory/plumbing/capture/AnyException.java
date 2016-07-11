package org.testory.plumbing.capture;

public class AnyException extends RuntimeException {
  public AnyException() {}

  public AnyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AnyException(String message, Throwable cause) {
    super(message, cause);
  }

  public AnyException(String message) {
    super(message);
  }

  public AnyException(Throwable cause) {
    super(cause);
  }
}
