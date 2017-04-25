package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.voidReturning;
import static org.testory.testing.Closures.voidThrowing;
import static org.testory.testing.Fakes.newThrowable;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.StackTraces.printStackTrace;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.VoidClosure;

public class TestWhenVoidClosure {
  private Throwable throwable;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
  }

  @Test
  public void inspects_closure_returning() {
    when(voidReturning());
    thenReturned();
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void inspects_closure_throwing() {
    when(voidThrowing(throwable));
    thenThrown(throwable);
  }

  @Test
  public void failure_prints_inspected_closure_returning() {
    when(voidReturning());
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but returned\n"
          + "    void\n"));
    }
  }

  @Test
  public void failure_prints_inspected_closure_throwing() {
    when(voidThrowing(throwable));
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but thrown\n"
          + "    " + throwable + "\n"));
      assertThat(e, hasMessageContaining(
          "\n" + printStackTrace(throwable) + "\n"));
    }
  }

  @Test
  public void closure_cannot_be_null() {
    try {
      when((VoidClosure) null);
      fail();
    } catch (TestoryException e) {}
  }
}
