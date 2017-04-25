package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.givenTimes;
import static org.testory.testing.Closures.returning;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class TestGivenTimes {
  private int times, counter, failTime;
  private Exception exception;

  @Before
  public void before() {
    times = 5;
    counter = 0;
    exception = new Exception("exception");
  }

  @Test
  public void calls_closure_many_times() {
    givenTimes(times, new Closure() {
      public Void invoke() {
        counter++;
        return null;
      }
    });
    assertEquals(times, counter);
  }

  @Test
  public void calls_closure_zero_times() {
    givenTimes(0, new Closure() {
      public Void invoke() {
        counter++;
        return null;
      }
    });
    assertEquals(0, counter);
  }

  @Test
  public void gently_propagates_throwable_thrown_by_closure() {
    times = 5;
    failTime = 3;
    try {
      givenTimes(times, new Closure() {
        public Void invoke() throws Throwable {
          counter++;
          if (counter == failTime) {
            throw exception;
          }
          return null;
        }
      });
    } catch (Throwable throwable) {
      assertTrue(throwable instanceof RuntimeException);
      assertSame(exception, throwable.getCause());
      assertEquals(failTime, counter);
      return;
    }
    fail();
  }

  @Test
  public void cannot_call_closure_negative_number_of_times() {
    try {
      givenTimes(-1, returning(null));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void closure_cannot_be_null() {
    try {
      givenTimes(times, (Closure) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void calls_invocation_many_times() {
    givenTimes(times, new Runnable() {
      public void run() {
        counter++;
      }
    }).run();
    assertEquals(times, counter);
  }

  @Test
  public void calls_invocation_zero_times() {
    times = 0;
    givenTimes(0, new Runnable() {
      public void run() {
        counter++;
      }
    }).run();
    assertEquals(times, counter);
  }

  @Test
  public void gently_propagates_throwable_thrown_by_invocation() {
    times = 5;
    failTime = 3;
    try {
      givenTimes(times, new Callable<Object>() {
        public Object call() throws Exception {
          counter++;
          if (counter == failTime) {
            throw exception;
          }
          return null;
        }
      }).call();
    } catch (Throwable throwable) {
      assertSame(exception, throwable);
      assertEquals(failTime, counter);
      return;
    }
    fail();
  }

  @Test
  public void cannot_call_invocation_negative_number_of_times() {
    try {
      givenTimes(-1, new Runnable() {
        public void run() {}
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void invocation_instance_cannot_be_null() {
    try {
      givenTimes(times, (Object) null);
      fail();
    } catch (TestoryException e) {}
  }
}
