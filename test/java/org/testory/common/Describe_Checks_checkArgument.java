package org.testory.common;

import static org.junit.Assert.fail;
import static org.testory.common.Checks.checkArgument;

import org.junit.Test;

public class Describe_Checks_checkArgument {
  @Test
  public void should_throw_illegal_argument_exception_if_condition_is_false() {
    try {
      checkArgument(false);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_do_nothing_if_condition_is_true() {
    checkArgument(true);
  }
}
