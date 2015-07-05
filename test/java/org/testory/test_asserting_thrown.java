package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.StackTraces.printStackTrace;

import org.junit.Before;
import org.junit.Test;
import org.testory.testing.Closures.Invoker;

public class test_asserting_thrown {
  private Throwable throwable, otherThrowable;
  private Object object;
  private Object matcher;
  private Invoker invoker;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
    invoker = new Invoker();
  }

  @Test
  public void asserts_throwing_same_type() throws Throwable {
    class ExpectedThrowable extends Throwable {}

    when(throwing(new ExpectedThrowable()));
    thenThrown(ExpectedThrowable.class);

    when(invoker).invoke(throwing(new ExpectedThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void asserts_throwing_subtype() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    class SubThrowable extends ExpectedThrowable {}

    when(throwing(new SubThrowable()));
    thenThrown(ExpectedThrowable.class);

    when(invoker).invoke(throwing(new SubThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void fails_throwing_supertype() throws Throwable {
    class SuperThrowable extends Throwable {}
    class ExpectedThrowable extends SuperThrowable {}
    throwable = new SuperThrowable();

    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void fails_throwing_unrelated_type() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    throwable = new OtherThrowable();

    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but thrown\n"
          + "    " + throwable + "\n"
          + "\n"
          + printStackTrace(throwable) + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void asserts_throwing_same_throwable() throws Throwable {
    when(throwing(throwable));
    thenThrown(throwable);

    when(invoker).invoke(throwing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void fails_throwing_not_same_throwable() throws Throwable {
    when(throwing(otherThrowable));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + throwable + "\n"
          + "  but thrown\n"
          + "    " + otherThrowable + "\n"
          + "\n"
          + printStackTrace(otherThrowable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(otherThrowable));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + throwable + "\n"
          + "  but thrown\n"
          + "    " + otherThrowable + "\n"
          + "\n"
          + printStackTrace(otherThrowable) + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void asserts_throwing_matching_throwable() throws Throwable {
    matcher = matcherSame(throwable);

    when(throwing(throwable));
    thenThrown(matcher);

    when(invoker).invoke(throwing(throwable));
    thenThrown(matcher);
  }

  @Test
  public void fails_throwing_mismatching_throwable() throws Throwable {
    matcher = matcherSame(throwable);

    when(throwing(otherThrowable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + matcher + "\n"
          + "  but thrown\n"
          + "    " + otherThrowable + "\n"
          + "\n"
          + printStackTrace(otherThrowable) + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(throwing(otherThrowable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + matcher + "\n"
          + "  but thrown\n"
          + "    " + otherThrowable + "\n"
          + "\n"
          + printStackTrace(otherThrowable) + "\n"
          , e.getMessage());
    }
  }

  @Test
  public void asserts_throwing() throws Throwable {
    when(throwing(throwable));
    thenThrown();

    when(invoker).invoke(throwing(throwable));
    thenThrown();
  }

  @Test
  public void fails_returning() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    matcher = matcherSame(throwable);

    when(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + ExpectedThrowable.class.getName() + "\n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }
    when(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + throwable + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(object));
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + throwable + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + throwable + "\n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }

    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    " + matcher + "\n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }

    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    \n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(invoker).invoke(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    \n"
          + "  but returned\n"
          + "    " + object + "\n"
          , e.getMessage());
    }

    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n"
          + "  expected thrown\n"
          + "    \n"
          + "  but returned\n"
          + "    void\n"
          , e.getMessage());
    }

  }

  @Test
  public void type_cannot_be_null() {
    try {
      thenThrown((Class<? extends Throwable>) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void throwable_cannot_be_null() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_any_object() {
    try {
      thenThrown(object);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      thenThrown((Object) null);
      fail();
    } catch (TestoryException e) {}
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
