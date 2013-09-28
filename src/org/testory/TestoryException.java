package org.testory;

public class TestoryException extends RuntimeException {
  private static final long serialVersionUID = -5222839249836069701L;

  public TestoryException() {}

  public TestoryException(String message) {
    super(message);
  }

  public TestoryException(Throwable cause) {
    super(cause);
  }

  public TestoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
