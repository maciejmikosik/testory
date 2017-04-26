package org.testory;

public class TestoryAssertionError extends AssertionError {
  public TestoryAssertionError() {}

  public TestoryAssertionError(String message) {
    super(message);
  }

  public static TestoryAssertionError assertionError(String message) {
    return new TestoryAssertionError(message);
  }
}
