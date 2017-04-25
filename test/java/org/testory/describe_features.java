package org.testory;

public class describe_features {
  public void diagnosing_with_hamcrest_matchers() {
    new test_asserting_dynamic_matcher().failure_diagnoses_mismatch();
    new test_asserting_returned_dynamic_matcher().failure_diagnoses_mismatch();
    new test_asserting_returned_dynamic_matcher().failure_skips_diagnosis_if_thrown();
    new test_asserting_thrown_dynamic_matcher().failure_diagnoses_mismatch();
    new test_asserting_thrown_dynamic_matcher().failure_skips_diagnosis_if_returned();
  }

  public void arrays() {
    new test_verifyings().failure_prints_actual_invocations_with_array_arguments();
  }
}
