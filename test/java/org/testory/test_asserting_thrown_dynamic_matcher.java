package org.testory;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.DynamicMatchers.same;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.HamcrestMatchers.diagnosed;
import static org.testory.testing.HamcrestMatchers.hamcrestDiagnosticMatcher;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_thrown_dynamic_matcher {
  private Throwable throwable, otherThrowable;
  private Object object;
  private Object matcher;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
  }

  @Test
  public void asserts_throwing_matching_throwable() {
    matcher = same(throwable);
    when(throwing(throwable));
    thenThrown(matcher);
  }

  @Test
  public void fails_throwing_mismatching_throwable() {
    matcher = same(throwable);
    when(throwing(otherThrowable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_object() {
    matcher = same(throwable);
    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void() {
    matcher = same(throwable);
    when(voidReturning());
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_matcher() {
    matcher = same(throwable);
    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected thrown\n"
          + "    " + matcher + "\n"));
    }
  }

  @Test
  public void failure_diagnoses_mismatch() {
    matcher = hamcrestDiagnosticMatcher();
    when(throwing(throwable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  diagnosis\n"
          + "    " + diagnosed(throwable) + "\n"));
    }
  }

  @Test
  public void failure_skips_diagnosis_if_returned() {
    matcher = hamcrestDiagnosticMatcher();
    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, not(hasMessageContaining("diagnosis\n")));
    }
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
}
