package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.printStackTrace;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.test.Testilities.Invoker;

public class Describe_Testory_thenReturned_Object {
  private String name;
  private Object object, otherObject;
  private Throwable throwable;
  private Object matcher;
  private Invoker invoker;

  @Before
  public void before() {
    name = "name";
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == object;
      }

      public String toString() {
        return "matcher";
      }
    };
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_returned_equal_object() {
    when(newObject(name));
    thenReturned(newObject(name));
  }

  @Test
  public void should_succeed_if_closure_returned_equal_object() {
    when(returning(newObject(name)));
    thenReturned(newObject(name));
  }

  @Test
  public void should_succeed_if_proxy_returned_equal_object() throws Throwable {
    when(invoker).invoke(returning(newObject(name)));
    thenReturned(newObject(name));
  }

  @Test
  public void should_succeed_if_returned_same_object() {
    when(object);
    thenReturned(object);
  }

  @Test
  public void should_succeed_if_closure_returned_same_object() {
    when(returning(object));
    thenReturned(object);
  }

  @Test
  public void should_succeed_if_proxy_returned_same_object() throws Throwable {
    when(invoker).invoke(returning(object));
    thenReturned(object);
  }

  @Test
  public void should_succeed_if_returned_equal_array() {
    when(new Object[] { object });
    thenReturned(new Object[] { object });
  }

  @Test
  public void should_succeed_if_closure_returned_equal_array() {
    when(returning(new Object[] { object }));
    thenReturned(new Object[] { object });
  }

  @Test
  public void should_succeed_if_proxy_returned_equal_array() throws Throwable {
    when(invoker).invoke(returning(new Object[] { object }));
    thenReturned(new Object[] { object });
  }

  @Test
  public void should_succeed_if_returned_equal_primitive() {
    when(4);
    thenReturned(4);
  }

  @Test
  public void should_succeed_if_closure_returned_equal_primitive() {
    when(returning(4));
    thenReturned(4);
  }

  @Test
  public void should_succeed_if_proxy_returned_equal_primitive() throws Throwable {
    when(invoker).invoke(returning(4));
    thenReturned(4);
  }

  @Test
  public void should_succeed_if_returned_expected_null() {
    when((Object) null);
    thenReturned((Object) null);
  }

  @Test
  public void should_succeed_if_closure_returned_expected_null() {
    when(returning(null));
    thenReturned((Object) null);
  }

  @Test
  public void should_succeed_if_proxy_returned_expected_null() throws Throwable {
    when(invoker).invoke(returning(null));
    thenReturned((Object) null);
  }

  @Test
  public void should_succeed_if_returned_matched() {
    when(object);
    thenReturned(matcher);
  }

  @Test
  public void should_succeed_if_closure_returned_matched() {
    when(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void should_succeed_if_proxy_returned_matched() throws Throwable {
    when(invoker).invoke(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void should_fail_if_returned_not_equal_object() {
    when(object);
    try {
      thenReturned(otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    otherObject\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned_not_equal_object() {
    when(returning(object));
    try {
      thenReturned(otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    otherObject\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_not_equal_object() throws Throwable {
    when(invoker).invoke(returning(object));
    try {
      thenReturned(otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    otherObject\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_nonnull_object() {
    when(object);
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned_nonnull_object() {
    when(returning(object));
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_nonnull_object() throws Throwable {
    when(invoker).invoke(returning(object));
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_unexpected_null() {
    when((Object) null);
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned_unexpected_null() {
    when(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_unexpected_null() throws Throwable {
    when(invoker).invoke(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_mismatched() {
    when(otherObject);
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned_mismatched() {
    when(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_mismatched() throws Throwable {
    when(invoker).invoke(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_not_equal_primitive() {
    when(5);
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    5\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned_not_equal_primitive() {
    when(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    5\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_not_equal_primitive() throws Throwable {
    when(invoker).invoke(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    5\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_void() {
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    void\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned_void_instead_of_null() {
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    void\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_thrown() {
    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown() throws Throwable {
    when(invoker).invoke(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }
}
