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
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;

public class TestThenReturned {
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void asserts_returning_object() {
    when(returning(object));
    thenReturned();
  }

  @Test
  public void asserts_returning_null() {
    when(returning(null));
    thenReturned();
  }

  @Test
  public void asserts_returning_void() {
    when(voidReturning());
    thenReturned();
  }

  @Test
  public void fails_throwing() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected() {
    when(throwing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected returned\n"
          + "    \n"));
    }
  }
}
