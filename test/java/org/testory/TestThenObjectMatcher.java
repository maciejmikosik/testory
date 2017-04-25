package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.testing.DynamicMatchers.same;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.diagnosed;
import static org.testory.testing.HamcrestMatchers.hamcrestDiagnosticMatcher;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class TestThenObjectMatcher {
  private Object object, otherObject;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @Test
  public void asserts_matching_object() {
    matcher = same(object);
    then(object, matcher);
  }

  @Test
  public void fails_for_not_matching_object() {
    matcher = same(object);
    try {
      then(otherObject, matcher);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_matcher_and_object() {
    matcher = same(object);
    try {
      then(otherObject, matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected\n"
          + "    " + matcher + "\n"
          + "  but was\n"
          + "    " + otherObject + "\n"));
    }
  }

  @Test
  public void failure_diagnoses_mismatch() {
    matcher = hamcrestDiagnosticMatcher();
    try {
      then(object, matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  diagnosis\n"
          + "    " + diagnosed(object) + "\n"));
    }
  }

  @Test
  public void matcher_cannot_be_any_object() {
    try {
      then(object, otherObject);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      then(object, null);
      fail();
    } catch (TestoryException e) {}
  }
}
