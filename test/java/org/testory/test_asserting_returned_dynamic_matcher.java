package org.testory;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
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
    matcher = same(object);
    when(returning(object));
    thenReturned(matcher);
  }

  @Test
  public void fails_returning_mismatching_object() {
    matcher = same(object);
    when(returning(otherObject));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void asserts_returning_matcher() {
    matcher = same(object);
    when(returning(matcher));
    thenReturned(matcher);
  }

  @Test
  public void fails_returning_void() {
    matcher = same(object);
    when(voidReturning());
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_throwing() {
    matcher = same(object);
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_matcher() {
    matcher = same(object);
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

  @Test
  public void failure_diagnoses_mismatch() {
    matcher = hamcrestDiagnosticMatcher();
    when(returning(object));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  diagnosis\n"
          + "    " + diagnosed(object) + "\n"));
    }
  }

  @Test
  public void failure_skips_diagnosis_if_thrown() {
    matcher = hamcrestDiagnosticMatcher();
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, not(hasMessageContaining("diagnosis\n")));
    }
  }
}
