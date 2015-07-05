package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.testing.Testilities.newObject;
import static org.testory.testing.Testilities.newThrowable;
import static org.testory.testing.Testilities.printStackTrace;
import static org.testory.testing.Testilities.returning;
import static org.testory.testing.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.testing.Testilities.Invoker;

public class test_asserting_returned {
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
    invoker = new Invoker();
  }

  @Test
  public void asserts_returning_equal_object() throws Throwable {
    when(newObject(name));
    thenReturned(newObject(name));

    when(returning(newObject(name)));
    thenReturned(newObject(name));

    when(invoker).invoke(returning(newObject(name)));
    thenReturned(newObject(name));
  }

  @Test
  public void asserts_returning_same_object() throws Throwable {
    when(object);
    thenReturned(object);

    when(returning(object));
    thenReturned(object);

    when(invoker).invoke(returning(object));
    thenReturned(object);
  }

  @Test
  public void asserts_returning_equal_primitive() throws Throwable {
    when(4);
    thenReturned(4);

    when(returning(4));
    thenReturned(4);

    when(invoker).invoke(returning(4));
    thenReturned(4);
  }

  @Test
  public void asserts_returning_equal_null() throws Throwable {
    when((Object) null);
    thenReturned((Object) null);

    when(returning(null));
    thenReturned((Object) null);

    when(invoker).invoke(returning(null));
    thenReturned((Object) null);
  }

  @Test
  public void fails_returning_not_equal_object() throws Throwable {
    when(otherObject);
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }

    when(returning(otherObject));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(otherObject));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_returning_not_equal_primitive() throws Throwable {
    when(5);
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    4\n"
          + "  but returned\n"
          + "    5\n"
          , e.getMessage());
    }

    when(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    4\n"
          + "  but returned\n"
          + "    5\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(5));
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    4\n"
          + "  but returned\n"
          + "    5\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_returning_object_instead_of_null() throws Throwable {
    when(object);
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    null\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(returning(object));
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    null\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(object));
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    null\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_returning_null_instead_of_object() throws Throwable {
    when((Object) null);
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    null\n"
          , e.getMessage());
    }

    when(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    null\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(null));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    null\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_returning_void_instead_of_object() {
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_returning_void_instead_of_null() {
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    null\n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }
  }

  @Test
  public void asserts_returning_matching_object() throws Throwable {
    matcher = matcherSame(object);

    when(object);
    thenReturned(matcher);

    when(returning(object));
    thenReturned(matcher);

    when(invoker).invoke(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void fails_returning_mismatching_object() throws Throwable {
    matcher = matcherSame(object);
    when(otherObject);
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }

    when(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    " + otherObject + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void asserts_returning() throws Throwable {
    when(object);
    thenReturned();

    when(returning(object));
    thenReturned();

    when(invoker).invoke(returning(object));
    thenReturned();

    when((Object) null);
    thenReturned();

    when(returning(null));
    thenReturned();

    when(invoker).invoke(returning(null));
    thenReturned();

    when(new Runnable() {
      public void run() {}
    }).run();
    thenReturned();
  }

  @Test
  public void fails_throwing() throws Throwable {
    matcher = matcherSame(object);

    when(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(throwable));
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + object + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + matcher + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    " + matcher + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    \n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected returned\n"
          + "    \n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }
  }

  private static Object matcherSame(final Object expected) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == expected;
      }

      public String toString() {
        return "matcherSame(" + expected + ")";
      }
    };
  }
}
