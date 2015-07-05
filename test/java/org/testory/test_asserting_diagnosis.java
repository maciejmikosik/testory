package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

public class test_asserting_diagnosis {
  private Object object;
  private Throwable throwable;
  private String mismatch;
  private Description description;
  private Matcher<?> matcher;

  @SuppressWarnings("hiding")
  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
    description = new StringDescription();
    matcher = new BaseMatcher<Object>() {
      public boolean matches(Object item) {
        return false;
      }

      public void describeMismatch(Object item, Description description) {
        description.appendText("hamcrestMatcher.describeMismatch(" + item + ")");
      }

      public void describeTo(Description description) {
        description.appendText("hamcrestMatcher.toString()");
      }
    };
  }

  @Test
  public void asserting_diagnoses_mismatch() {
    matcher.describeMismatch(object, description);
    mismatch = description.toString();

    try {
      then(object, matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  diagnosis\n"
          + "    " + mismatch + "\n"));
    }
  }

  @Test
  public void asserting_returned_diagnoses_mismatch() {
    matcher.describeMismatch(object, description);
    mismatch = description.toString();

    when(returning(object));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  diagnosis\n"
          + "    " + mismatch + "\n"));
    }
  }

  @Test
  public void asserting_returned_skips_diagnosis_if_thrown() {
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertFalse(e.getMessage(), e.getMessage().contains("diagnosis\n"));
    }
  }

  @Test
  public void asserting_thrown_diagnoses_mismatch() {
    matcher.describeMismatch(throwable, description);
    mismatch = description.toString();

    when(throwing(throwable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  diagnosis\n"
          + "    " + mismatch + "\n"));
    }
  }

  @Test
  public void asserting_thrown_skips_diagnosis_if_returned() {
    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertFalse(e.getMessage(), e.getMessage().contains("diagnosis\n"));
    }
  }
}
