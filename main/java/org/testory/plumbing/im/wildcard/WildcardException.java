package org.testory.plumbing.im.wildcard;

public class WildcardException extends RuntimeException {
  public WildcardException() {}

  public WildcardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public WildcardException(String message, Throwable cause) {
    super(message, cause);
  }

  public WildcardException(String message) {
    super(message);
  }

  public WildcardException(Throwable cause) {
    super(cause);
  }
}
