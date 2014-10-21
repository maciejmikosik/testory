package org.testory.proxy;

public class ProxyException extends RuntimeException {
  public ProxyException() {}

  public ProxyException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProxyException(String message) {
    super(message);
  }

  public ProxyException(Throwable cause) {
    super(cause);
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw new ProxyException();
    }
  }
}
