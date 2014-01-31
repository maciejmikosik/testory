package org.testory;

import static org.testory.Testory.givenTest;

import org.junit.Test;

public class Describe_final_methods {
  @SuppressWarnings("unused")
  @Test
  public void should_inject_mock_with_final_hashcode() {
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
