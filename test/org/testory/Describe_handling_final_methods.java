package org.testory;

import static org.testory.Testory.givenTest;

import org.junit.Test;

public class Describe_handling_final_methods {
  // TODO test stubbing and verification with final methods
  @SuppressWarnings("unused")
  @Test
  public void given_test_injects_mock_with_final_hashcode() {
    class Foo {
      private boolean initialized = false;

      public Foo() {
        initialized = true;
      }

      public final int hashCode() {
        if (!initialized) {
          throw new RuntimeException();
        }
        return 0;
      }
    }
    class TestClass {
      Foo foo;
    }
    TestClass test = new TestClass();
    givenTest(test);
  }
}
