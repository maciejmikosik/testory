package org.testory.proxy;

public class ProxyException extends RuntimeException {
  private static final long serialVersionUID = 3886006358788556231L;

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
}
