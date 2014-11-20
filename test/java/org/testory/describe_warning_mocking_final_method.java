package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class describe_warning_mocking_final_method {

  @Before
  public void before() {
    triggerPurge();
  }

  private static void triggerPurge() {
    when("");
    when("");
  }

  @Test
  public void warns_about_mocking_class_with_final_method() {
    class ClassWithFinalMethod {
      @SuppressWarnings("unused")
      public final void finalMethod() {}
    }
    try {
      mock(ClassWithFinalMethod.class);
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("\n\n  warnings\n"));
      assertTrue(
          e.getMessage(),
          e.getMessage().contains(
              "class " + ClassWithFinalMethod.class.getSimpleName() + " has final method" + "\n"));
    }
  }

  @Test
  public void mocking_object_generates_no_warnings() {
    mock(Object.class);
    try {
      then(false);
      fail();
    } catch (TestoryAssertionError e) {
      assertFalse(e.getMessage(), e.getMessage().contains("warnings\n"));
    }
  }
}
