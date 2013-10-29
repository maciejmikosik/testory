package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

@SuppressWarnings("serial")
public class Describe_Testory_thenThrown {
  private Throwable throwable, otherThrowable;
  private Object object;
  private Object matcher;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    otherThrowable = newThrowable("otherThrowable");
    object = newObject("object");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == throwable;
      }

      public String toString() {
        return "matcher";
      }
    };
  }

  @Test
  public void should_succeed_if_thrown_expected_throwable() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    thenThrown(throwable);
  }

  @Test
  public void should_fail_if_throw_throwable_not_equal_to_expected() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw otherThrowable;
      }
    });
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but thrown\n" //
          + "    otherThrowable\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_instead_of_thrown_expected_throwable() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      thenThrown(throwable);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    throwable\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_thrown_throwable_of_expected_type() {
    class ExpectedThrowable extends Throwable {}
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw new ExpectedThrowable();
      }
    });
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_succeed_if_throw_throwable_subtyping_expected_type() {
    class ExpectedThrowable extends Throwable {}
    class SubThrowable extends ExpectedThrowable {}
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw new SubThrowable();
      }
    });
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_fail_if_thrown_throwable_not_related_to_expected_type() {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    otherThrowable = new OtherThrowable();
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw otherThrowable;
      }
    });
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + OtherThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_throwable_supertyping_expected_type() {
    class SuperThrowable extends Throwable {}
    class ExpectedThrowable extends SuperThrowable {}
    otherThrowable = new SuperThrowable();
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw otherThrowable;
      }
    });
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + SuperThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_instead_of_thrown_throwable_of_expected_type() {
    class ExpectedThrowable extends Throwable {}
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_thrown_throwable_matched_by_matcher() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    thenThrown(matcher);
  }

  @Test
  public void should_fail_if_thrown_throwable_not_matched_by_matcher() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw otherThrowable;
      }
    });
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
          + "  but thrown\n" //
          + "    otherThrowable\n" //
          + "\n" //
          + printStackTrace(otherThrowable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_instead_of_thrown_throwable_matching_matcher() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      thenThrown(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown throwable matching\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_throw_when_expected_thrown_anything() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    thenThrown();
  }

  @Test
  public void should_fail_if_returned_when_expected_thrown_anything() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown\n" //
          + "    \n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_expected_null_throwable() {
    try {
      thenThrown((Throwable) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_if_expected_throwable_of_null_type() {
    try {
      thenThrown((Class<? extends Throwable>) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_if_cannot_downcast_matcher() {
    try {
      thenThrown(object);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_if_expected_throwable_matching_null_matcher() {
    try {
      thenThrown((Object) null);
      fail();
    } catch (TestoryException e) {}
  }

  private static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
