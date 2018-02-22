package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
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

public class TestThenThrownMatcher {
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
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but thrown\n"
              + "    %s\n"
              + "\n"
              + "%s",
              matcher,
              otherThrowable,
              printStackTrace(otherThrowable)),
          e.getMessage());
    }
  }

  @Test
  public void diagnoses_throwing_mismatching_throwable() {
    matcher = hamcrestDiagnosticMatcher();
    when(throwing(throwable));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but thrown\n"
              + "    %s\n"
              + "  diagnosis\n"
              + "    %s\n"
              + "\n"
              + "%s",
              matcher,
              throwable,
              diagnosed(throwable),
              printStackTrace(throwable)),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_object() {
    matcher = same(throwable);
    when(returning(object));
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but returned\n"
              + "    %s\n",
              matcher,
              object),
          e.getMessage());
    }
  }

  @Test
  public void fails_returning_void() {
    matcher = same(throwable);
    when(voidReturning());
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(
          format("\n"
              + "  expected thrown\n"
              + "    %s\n"
              + "  but returned\n"
              + "    void\n",
              matcher),
          e.getMessage());
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
