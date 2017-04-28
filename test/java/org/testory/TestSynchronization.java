package org.testory;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;

public class TestSynchronization {
  private Object mock;
  private String string;
  private Object returned;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void invocation_is_visible_by_other_thread() {
    runInOtherThread(new Runnable() {
      public void run() {
        mock.toString();
      }
    });
    thenCalled(mock).toString();
  }

  @Test
  public void stubbing_is_visible_by_other_thread() {
    given(willReturn(string), mock).toString();
    runInOtherThread(new Runnable() {
      public void run() {
        returned = mock.toString();
      }
    });
    thenEqual(returned, string);
  }

  private static void runInOtherThread(Runnable runnable) {
    try {
      Thread thread = new Thread(runnable);
      thread.start();
      thread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
