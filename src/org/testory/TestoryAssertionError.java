package org.testory;

public class TestoryAssertionError extends AssertionError {
  private static final long serialVersionUID = -4502209966554449732L;

  public TestoryAssertionError() {}

  public TestoryAssertionError(String message) {
    super(message);
  }
}
