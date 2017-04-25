package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.then;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Test;

public class TestThenBoolean {
  @Test
  public void asserts_true_condition() {
    then(true);
  }

  @Test
  public void fails_for_false_condition() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected() {
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  expected\n"
          + "    true\n"
          + "  but was\n"
          + "    false\n"));
    }
  }
}
