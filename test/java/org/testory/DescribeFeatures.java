package org.testory;

public class DescribeFeatures {
  public void diagnosing_with_hamcrest_matchers() {
    new TestThenObjectMatcher().diagnoses_mismatch();
    new TestThenReturnedMatcher().diagnoses_mismatch();
    new TestThenThrownMatcher().diagnoses_throwing_mismatching_throwable();
  }

  public void arrays() {
    new TestThenCalledAllOverloadings().failure_prints_actual_invocations_with_array_arguments();
  }
}
