package org.testory;

import static org.junit.Assert.fail;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.givenTimes;
import static org.testory.Testory.givenTry;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;

import org.junit.Test;

public class TestFinalKeyword {
  @Test
  public void when_does_not_proxy_final_class() {
    final class FinalClass {}
    try {
      when(new FinalClass()).toString();
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void cannot_mock_final_class() {
    final class FinalClass {}
    try {
      mock(FinalClass.class);
      fail();
    } catch (TestoryException e) {}
  }

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

  @SuppressWarnings("unused")
  @Test
  public void final_object_methods_are_not_prestubbed_but_also_not_accidently_invoked() {
    class Foo {
      public final boolean equals(Object obj) {
        throw new RuntimeException();
      }

      public final int hashCode() {
        throw new RuntimeException();
      }

      public final String toString() {
        throw new RuntimeException();
      }
    }

    mock(Foo.class);

    class TestClass {
      Foo foo;
    }
    givenTest(new TestClass());
  }
}
