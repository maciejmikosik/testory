package org.testory.testing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testory.common.Closure;
import org.testory.testing.Closures.Invoker;

public class TestClosuresInvoker {
  private Closure closure;
  private int count;

  @Test
  public void invokes_closure_once() throws Throwable {
    closure = new Closure() {
      public Object invoke() throws Throwable {
        return count++;
      }
    };
    new Invoker().invoke(closure);
    assertEquals(1, count);
  }
}
