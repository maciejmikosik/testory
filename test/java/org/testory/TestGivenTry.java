package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.givenTry;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

public class TestGivenTry {
  private int counter;
  private Exception exception;

  @Before
  public void before() {
    exception = new Exception("exception");
  }

  @Test
  public void calls_method_once() {
    givenTry(new Runnable() {
      public void run() {
        counter++;
      }
    }).run();
    assertEquals(1, counter);
  }

  @Test
  public void catches_throwable() throws Exception {
    givenTry(new Callable<Object>() {
      public Object call() throws Exception {
        throw exception;
      }
    }).call();
  }

  @Test
  public void invocation_instance_cannot_be_null() {
    try {
      givenTry(null);
      fail();
    } catch (TestoryException e) {}
  }
}
