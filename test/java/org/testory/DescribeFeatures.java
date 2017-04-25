package org.testory;

public class DescribeFeatures {
  public void diagnosing_with_hamcrest_matchers() {
    new TestThenObjectMatcher().failure_diagnoses_mismatch();
    new TestThenReturnedMatcher().failure_diagnoses_mismatch();
    new TestThenReturnedMatcher().failure_skips_diagnosis_if_thrown();
    new TestThenThrownMatcher().failure_diagnoses_mismatch();
    new TestThenThrownMatcher().failure_skips_diagnosis_if_returned();
  }

  public void arrays() {
    new TestThenCalledAllOverloadings().failure_prints_actual_invocations_with_array_arguments();
  }
}
