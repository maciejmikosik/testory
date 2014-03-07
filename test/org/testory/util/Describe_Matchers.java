package org.testory.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.test.Testilities.newObject;
import static org.testory.util.Matchers.asMatcher;
import static org.testory.util.Matchers.isMatcher;

import org.fest.assertions.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Describe_Matchers {
  private Object matcher, object;
  private String string;

  @Before
  public void before() {
    matcher = newObject("matcher");
    object = newObject("object");
    string = "string";
  }

  @Test
  public void should_hamcrest_matcher_be_matcher() {
    matcher = new Matcher<Object>() {
      public boolean matches(Object item) {
        return false;
      }

      public void describeTo(Description description) {}

      public void describeMismatch(Object item, Description mismatchDescription) {}

      @Deprecated
      public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_hamcrest_matcher_match_matching_item() {
    matcher = new Matcher<Object>() {
      public boolean matches(Object item) {
        return object == item;
      }

      public void describeTo(Description description) {}

      public void describeMismatch(Object item, Description mismatchDescription) {}

      @Deprecated
      public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_hamcrest_matcher_not_match_not_matching_item() {
    matcher = new Matcher<Object>() {
      public boolean matches(Object item) {
        return object != item;
      }

      public void describeTo(Description description) {}

      public void describeMismatch(Object item, Description mismatchDescription) {}

      @Deprecated
      public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_fest_condition_be_matcher() {
    matcher = new Condition<Object>() {
      public boolean matches(Object value) {
        return false;
      }
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_fest_condition_match_matching_item() {
    matcher = new Condition<Object>() {
      public boolean matches(Object value) {
        return value == object;
      }
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_fest_condition_not_match_not_matching_item() {
    matcher = new Condition<Object>() {
      public boolean matches(Object value) {
        return value != object;
      }
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_guava_predicate_be_matcher() {
    matcher = new Predicate<Object>() {
      public boolean apply(Object input) {
        return false;
      }
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_guava_predicate_match_matching_item() {
    matcher = new Predicate<Object>() {
      public boolean apply(Object input) {
        return input == object;
      }
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_guava_predicate_not_match_not_matching_item() {
    matcher = new Predicate<Object>() {
      public boolean apply(Object input) {
        return input != object;
      }
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_guava_function_be_matcher() {
    matcher = new Function<Object, Boolean>() {
      public Boolean apply(Object input) {
        return false;
      }
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_guava_function_match_matching_item() {
    matcher = new Function<Object, Boolean>() {
      public Boolean apply(Object input) {
        return input == object;
      }
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_guava_function_not_match_not_matching_item() {
    matcher = new Function<Object, Boolean>() {
      public Boolean apply(Object input) {
        return input != object;
      }
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_object_with_matches_method_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return false;
      }
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_object_with_matches_method_match_matching_item() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == object;
      }
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_object_with_matches_method_not_match_not_matching_item() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item != object;
      }
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_object_with_apply_method_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean apply(Object item) {
        return false;
      }
    };
    assertTrue(isMatcher(matcher));
  }

  @Test
  public void should_object_with_apply_method_match_matching_item() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean apply(Object input) {
        return input == object;
      }
    };
    assertTrue(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_object_with_apply_method_not_match_not_matching_item() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean apply(Object input) {
        return input != object;
      }
    };
    assertFalse(asMatcher(matcher).matches(object));
  }

  @Test
  public void should_allow_null_item() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean apply(Object input) {
        return input == null;
      }
    };
    assertTrue(asMatcher(matcher).matches(null));
  }

  @Test
  public void should_object_with_method_with_wrong_name_not_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean wrongName(Object item) {
        return false;
      }
    };
    assertFalse(isMatcher(matcher));
  }

  @Test
  public void should_object_with_method_returning_void_not_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public void matches(Object item) {}
    };
    assertFalse(isMatcher(matcher));
  }

  @Test
  public void should_object_with_method_accepting_not_object_not_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(String item) {
        return false;
      }
    };
    assertFalse(isMatcher(matcher));
  }

  @Test
  public void should_object_with_not_public_method_not_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      boolean matches(Object item) {
        return false;
      }
    };
    assertFalse(isMatcher(matcher));
  }

  @Test
  public void should_object_with_method_throwing_exception_not_be_matcher() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) throws Exception {
        return false;
      }
    };
    assertFalse(isMatcher(matcher));
  }

  @Test
  public void should_object_without_matching_method_fail() {
    try {
      asMatcher(matcher);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_delegate_to_string() {
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return false;
      }

      public String toString() {
        return string;
      }
    };
    assertEquals(string, asMatcher(matcher).toString());
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
