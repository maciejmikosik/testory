package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.test.TestUtils.newObject;
import static org.testory.test.TestUtils.newThrowable;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class Describe_Testory_thenReturned {
  private Object object, otherObject;
  private Throwable throwable;
  private Object matcher;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    throwable = newThrowable("throwable");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == object;
      }

      public String toString() {
        return "matcher";
      }
    };
  }

  @Test
  public void should_succeed_if_returned_object_equal_to_expected_object() {
    when(new Integer(10));
    thenReturned(new Integer(10));
  }

  @Test
  public void should_succeed_if_returned_object_same_as_expected_object() {
    when(object);
    thenReturned(object);
  }

  @Test
  public void should_fail_if_returned_object_not_equal_to_expected_object() {
    when(object);
    try {
      thenReturned(otherObject);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    otherObject\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_object() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_array_equal_to_expected_object() {
    when(new Object[] { object });
    thenReturned(new Object[] { object });
  }

  @Test
  public void should_succeed_if_returned_null_equal_to_expected_null_object() {
    when((Object) null);
    thenReturned((Object) null);
  }

  @Test
  public void should_fail_if_returned_object_not_equal_to_expected_null_object() {
    when(object);
    try {
      thenReturned((Object) null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_null_not_equal_to_expected_object() {
    when((Object) null);
    try {
      thenReturned(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    object\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_object_matched_by_matcher() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    thenReturned(matcher);
  }

  @Test
  public void should_fail_if_returned_object_not_matched_by_matcher() {
    when(new Closure() {
      public Object invoke() {
        return otherObject;
      }
    });
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but returned\n" //
          + "    otherObject\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_object_matched_by_matcher() {
    try {
      when(new Closure() {
        public Object invoke() throws Throwable {
          throw throwable;
        }
      });
    } catch (Throwable e) {}
    try {
      thenReturned(matcher);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    matcher\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_expected_primitive() {
    when(new Closure() {
      public Integer invoke() {
        return 4;
      }
    });
    thenReturned(4);
  }

  @Test
  public void should_fail_if_returned_not_expected_primitive() {
    when(new Closure() {
      public Integer invoke() {
        return 5;
      }
    });
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    5\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_returned_null_instead_of_expected_primitive() {
    when(new Closure() {
      public Integer invoke() {
        return null;
      }
    });
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but returned\n" //
          + "    null\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_primitive() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    try {
      thenReturned(4);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    4\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_expected_null() {
    when(new Closure() {
      public Object invoke() {
        return null;
      }
    });
    thenReturned(null);
  }

  @Test
  public void should_fail_if_returned_not_expected_null() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_thrown_instead_of_returned_expected_null() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    try {
      thenReturned(null);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    null\n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_succeed_if_returned_when_expected_returned_anything() {
    when(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    thenReturned();
  }

  @Test
  public void should_succeed_if_returned_null_when_expected_returned_anything() {
    when(new Closure() {
      public Object invoke() {
        return null;
      }
    });
    thenReturned();
  }

  @Test
  public void should_fail_if_thrown_when_expected_returned_anything() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    });
    try {
      thenReturned();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected returned\n" //
          + "    \n" //
          + "  but thrown\n" //
          + "    throwable\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  private static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
