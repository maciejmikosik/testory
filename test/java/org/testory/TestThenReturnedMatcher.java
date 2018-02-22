package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.DynamicMatchers.same;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.HamcrestMatchers.diagnosed;
import static org.testory.testing.HamcrestMatchers.hamcrestDiagnosticMatcher;

import org.junit.Before;
import org.junit.Test;

public class TestThenReturnedMatcher {
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
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but returned\n"
              + "    %s\n",
              matcher,
              otherObject),
          e.getMessage());
    }
  }

  @Test
  public void diagnoses_mismatch() {
    matcher = hamcrestDiagnosticMatcher();
    when(returning(object));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but returned\n"
              + "    %s\n"
              + "  diagnosis\n"
              + "    %s\n",
              matcher,
              object,
              diagnosed(object)),
          e.getMessage());
    }
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
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but returned\n"
              + "    void\n",
              matcher),
          e.getMessage());
    }
  }

  @Test
  public void fails_throwing() {
    matcher = same(object);
    when(throwing(throwable));
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected returned\n"
              + "    %s\n"
              + "  but thrown\n"
              + "    %s\n"
              + "\n"
              + "%s",
              matcher,
              throwable,
              printStackTrace(throwable)),
          e.getMessage());
    }
  }
}
