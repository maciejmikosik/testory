package org.testory;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import java.lang.Thread.UncaughtExceptionHandler;

import org.junit.Before;
import org.junit.Test;

/** more tests in then* versions */
public class Describe_Testory_when {
  private Throwable throwable;
  private Runnable runnable;
  private Object object;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
  }

  @Test
  public void should_proxy_instance() {
    class Foo {}
    object = new Foo();
    Object when = when(object);
    assertNotSame(object, when);
    assertTrue(when instanceof Foo);
  }

  @Test
  public void should_proxy_catch_thrown_throwable() {
    runnable = new Runnable() {
      public void run() {
        throw new RuntimeException();
      }
    };
    when(runnable).run();
  }

  @Test
  public void should_not_proxy_instance_of_final_class() {
    final class FinalClass {}
    FinalClass finalInstance = new FinalClass();
    FinalClass when = when(finalInstance);
    assertNull(when);
  }

  @Test
  public void should_not_proxy_null_instance() {
    Object when = when((Object) null);
    assertNull(when);
  }

  @Test
  public void should_accept_object() {
    when(object);
  }

  @Test
  public void should_accept_null_object() {
    when((Object) null);
  }

  @Test
  public void should_accept_closure_returning() {
    when(returning(object));
  }

  @Test
  public void should_accept_closure_throwing() {
    when(throwing(throwable));
  }

  @Test
  public void should_fail_if_missed_when() throws Throwable {
    Thread thread = new Thread() {
      public void run() {
        thenReturned(object);
      }
    };
    thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        throwable = e;
      }
    });
    thread.start();
    thread.join();
    assertTrue(throwable instanceof TestoryException);
  }

  @Test
  public void should_fail_for_null_closure() {
    try {
      when((Closure) null);
      fail();
    } catch (TestoryException e) {}
  }
}
