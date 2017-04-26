package org.testory;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.HamcrestMatchers.hasMessage;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import java.lang.Thread.UncaughtExceptionHandler;

import org.junit.Before;
import org.junit.Test;

public class TestWhen {
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void inspects_object() {
    when(object);
    thenReturned(object);
  }

  @Test
  public void inspects_null_object() {
    when((Object) null);
    thenReturned(null);
  }

  @Test
  public void inspects_primitive() {
    when(1234);
    thenReturned(1234);
  }

  @Test
  public void failure_prints_inspected_object() {
    when(object);
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but returned\n"
          + "    " + object + "\n"));
    }
  }

  @Test
  public void failure_prints_inspected_null() {
    when((Object) null);
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but returned\n"
          + "    " + null + "\n"));
    }
  }

  @Test
  public void failure_prints_inspected_primitive() {
    when(1234);
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but returned\n"
          + "    " + 1234 + "\n"));
    }
  }

  @Test
  public void when_is_required_for_result_assertion() throws Throwable {
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

    assertThat(throwable, instanceOf(TestoryException.class));
    assertThat(throwable, hasMessage("must call when"));
  }
}
