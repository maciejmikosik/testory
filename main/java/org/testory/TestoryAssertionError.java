package org.testory;

import org.testory.proxy.proxer.CglibProxer;

public class TestoryAssertionError extends AssertionError {
  public TestoryAssertionError() {}

  public TestoryAssertionError(String message) {
    super(message);
  }

  public static TestoryAssertionError assertionError(String message) {
    TestoryAssertionError original = new TestoryAssertionError();
    TestoryAssertionError caller = new TestoryAssertionError(message);
    caller.initCause(original);
    caller.setStackTrace(callerTrace(original.getStackTrace()));
    return caller;
  }

  private static StackTraceElement[] callerTrace(StackTraceElement[] stackTrace) {
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      // TODO implement more generic way that handles other Proxers
      String name = stackTrace[i].getClassName();
      if (name.equals(Testory.class.getName()) || name.startsWith(CglibProxer.class.getName())) {
        for (int j = i + 1; j < stackTrace.length; j++) {
          if (stackTrace[j].getLineNumber() >= 0) {
            return new StackTraceElement[] { stackTrace[j] };
          }
        }
        throw new Error();
      }
    }
    throw new Error();
  }
}
