package org.testory.testing;

import org.testory.common.Closure;
import org.testory.common.VoidClosure;

public class Closures {
  public static Closure returning(final Object object) {
    return new Closure() {
      public Object invoke() {
        return object;
      }
    };
  }

  public static Closure throwing(final Throwable throwable) {
    if (throwable == null) {
      throw new NullPointerException();
    }
    return new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    };
  }

  public static VoidClosure voidReturning() {
    return new VoidClosure() {
      public void invoke() {}
    };
  }

  public static VoidClosure voidThrowing(final Throwable throwable) {
    if (throwable == null) {
      throw new NullPointerException();
    }
    return new VoidClosure() {
      public void invoke() throws Throwable {
        throw throwable;
      }
    };
  }

  public static class Invoker {
    public Object invoke(Closure closure) throws Throwable {
      return closure.invoke();
    }
  }
}
