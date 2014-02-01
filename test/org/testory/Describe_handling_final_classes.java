package org.testory;

import static org.junit.Assert.fail;
import static org.testory.Testory.givenTimes;
import static org.testory.Testory.givenTry;

import org.junit.Test;

public class Describe_handling_final_classes {
  @Test
  public void given_try_forbids_final_class() {
    final class FinalClass {}
    try {
      givenTry(new FinalClass());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void given_times_forbids_final_class() {
    final class FinalClass {}
    try {
      givenTimes(3, new FinalClass());
      fail();
    } catch (TestoryException e) {}
  }
}
