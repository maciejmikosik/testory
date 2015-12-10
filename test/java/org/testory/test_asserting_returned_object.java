package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_returned_object {
  private String name;
  private Object object, otherObject;
  private Throwable throwable;

  @Before
  public void before() {
    name = "name";
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
  }

  @Test
  public void asserts_returning_equal_object() {
    when(returning(newObject(name)));
    thenReturned(newObject(name));
  }

  @Test
  public void asserts_returning_same_object() {
    when(returning(object));
    thenReturned(object);
  }

  @Test
  public void asserts_returning_equal_primitive() {
    when(returning(4));
    thenReturned(4);
  }

  @Test
  public void asserts_returning_equal_null() {
    when(returning(null));
    thenReturned((Object) null);
  }

  @Test
  public void fails_returning_not_equal_object() {
    when(returning(otherObject));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_not_equal_primitive() {
    when(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_object_instead_of_null() {
    when(returning(object));
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_null_instead_of_object() {
    when(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void_instead_of_null() {
    // TODO replace by VoidClosure
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_throwing() {
    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_object() {
    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  expected returned\n"
          + "    " + object + "\n"));
    }
  }

  @Test
  public void failure_prints_expected_null() {
    when(throwing(throwable));
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  expected returned\n"
          + "    " + null + "\n"));
    }
  }

  @Test
  public void failure_prints_expected_primitive() {
    when(throwing(throwable));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  expected returned\n"
          + "    " + 4 + "\n"));
    }
  }
}
