package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Matchers_asMatcher {
  private Object matcher, object, otherObject;
  private String string;

  @Before
  public void before() {
    matcher = newObject("matcher");
    object = newObject("object");
    otherObject = newObject("otherObject");
    string = "string";
  }

  @Test
  public void supports_hamcrest_matcher() {
    matcher = new org.hamcrest.Matcher<Object>() {
      public boolean matches(Object item) {
        return item == object;
      }

      public void describeTo(org.hamcrest.Description description) {}

      public void describeMismatch(Object item, org.hamcrest.Description mismatchDescription) {}

      @Deprecated
      public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_fest_matcher() {
    matcher = new org.fest.assertions.Condition<Object>() {
      public boolean matches(Object value) {
        return value == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_fest_2_matcher() {
    matcher = new org.fest.assertions.core.Condition<Object>() {
      public boolean matches(Object value) {
        return value == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_assertj_matcher() {
    matcher = new org.assertj.core.api.Condition<Object>() {
      public boolean matches(Object value) {
        return value == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_guava_predicate() {
    matcher = new com.google.common.base.Predicate<Object>() {
      public boolean apply(Object input) {
        return input == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_guava_function() {
    matcher = new com.google.common.base.Function<Object, Boolean>() {
      public Boolean apply(Object input) {
        return input == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_matches_method() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void supports_apply_method() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean apply(Object item) {
        return item == object;
      }
    };
    assertTrue(isMatcher(matcher));
    assertTrue(asMatcher(matcher).matches(object));
    assertFalse(asMatcher(matcher).matches(otherObject));
  }

  @Test
  public void requires_method() {
    assertFalse(isMatcher(new Object() {}));
  }

  @Test
  public void requires_correct_name() {
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public boolean wrongName(Object item) {
        return false;
      }
    }));
  }

  @Test
  public void requires_boolean_return_type() {
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public void matches(Object item) {}
    }));
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public Object matches(Object item) {
        return null;
      }
    }));
  }

  @Test
  public void requires_object_parameter() {
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public boolean matches(String item) {
        return false;
      }
    }));
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public boolean matches() {
        return false;
      }
    }));
  }

  @Test
  public void requires_public_method() {
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      boolean matches(Object item) {
        return false;
      }
    }));
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      protected boolean matches(Object item) {
        return false;
      }
    }));
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      private boolean matches(Object item) {
        return false;
      }
    }));
  }

  @Test
  public void requires_no_checked_exceptions() {
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) throws Exception {
        return false;
      }
    }));
    assertFalse(isMatcher(new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) throws Throwable {
        return false;
      }
    }));
  }

  @Test
  public void delegates_to_string() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return false;
      }

      public String toString() {
        return string;
      }
    };
    assertEquals(matcher.toString(), asMatcher(matcher).toString());
  }

  @Test
  public void object_cannot_be_matcher() {
    assertFalse(isMatcher(object));
    try {
      asMatcher(object);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void null_cannot_be_matcher() {
    try {
      isMatcher(null);
      fail();
    } catch (NullPointerException e) {}
    try {
      asMatcher(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
