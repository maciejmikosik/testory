package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

public class Closures {
  public static Closure invoked(Closure closure) {
    checkNotNull(closure);
    final Object object;
    try {
      object = closure.invoke();
    } catch (final Throwable throwable) {
      return new Closure() {
        public Object invoke() throws Throwable {
          throw throwable;
        }
      };
    }
    return new Closure() {
      public Object invoke() {
        return object;
      }
    };
  }
}
