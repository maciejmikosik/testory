package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.Matchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_returned_dynamic_matcher {
  private Object object, otherObject;
  private Throwable throwable;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
  }

  @Test
  public void asserts_returning_matching_object() {
    matcher = matcherSame(object);
    when(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void fails_returning_mismatching_object() {
    matcher = matcherSame(object);
    when(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void() {
    matcher = matcherSame(object);
    when(voidReturning());
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_throwing() {
    matcher = matcherSame(object);
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_matcher() {
    matcher = matcherSame(object);
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected returned\n"
          + "    " + matcher + "\n"));
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
