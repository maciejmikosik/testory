package org.testory;

public class TestoryException extends RuntimeException {
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
