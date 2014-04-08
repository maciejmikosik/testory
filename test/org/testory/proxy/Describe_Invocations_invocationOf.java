package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Objects.areEqual;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invocationOf;
import static org.testory.test.Testilities.newObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Matcher;
import org.testory.common.Nullable;

public class Describe_Invocations_invocationOf {
  private Method method, otherMethod;
  private Object argument, otherArgument;
  private List<Object> arguments, otherArguments;
  private Instance instance, otherInstance;
  private Matcher matcherA, matcherB, matcherC;
  private Matcher matcher;

  @Before
  public void before() throws Exception {
    method = Instance.class.getDeclaredMethod("method", Object.class);
    otherMethod = Instance.class.getDeclaredMethod("otherMethod", Object.class);
    instance = new Instance();
    otherInstance = new Instance();
    argument = newObject("argument");
    otherArgument = newObject("otherArgument");
    arguments = Arrays.asList(argument);
    otherArguments = Arrays.asList(otherArgument);
    matcherA = newMatcher("matcherA");
    matcherB = newMatcher("matcherB");
    matcherC = newMatcher("matcherC");
  }

  @Test
  public void requires_matching_components() {
    matcher = invocationOf(same(method), same(instance), equal(arguments));
    assertTrue(matcher.matches(invocation(method, instance, arguments)));
    assertFalse(matcher.matches(invocation(otherMethod, instance, arguments)));
    assertFalse(matcher.matches(invocation(method, otherInstance, arguments)));
    assertFalse(matcher.matches(invocation(method, instance, otherArguments)));
  }

  @Test
  public void prints_matchers() {
    matcher = invocationOf(matcherA, matcherB, matcherC);
    assertEquals("invocationOf(" + matcherA + ", " + matcherB + ", " + matcherC + ")",
        matcher.toString());
  }

  @Test
  public void fails_for_null_components() {
    try {
      invocationOf(null, matcherB, matcherC);
      fail();
    } catch (NullPointerException e) {}
    try {
      invocationOf(matcherA, null, matcherC);
      fail();
    } catch (NullPointerException e) {}
    try {
      invocationOf(matcherA, matcherB, null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static Matcher same(final Object instance) {
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
        return instance == item;
      }

      public String toString() {
        return "same(" + instance + ")";
      }
    };
  }

  private static Matcher equal(final Object instance) {
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
        return areEqual(instance, item);
      }

      public String toString() {
        return "equal(" + instance + ")";
      }
    };
  }

  private static Matcher newMatcher(final String name) {
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
        return false;
      }

      public String toString() {
        return name;
      }
    };
  }

  private static class Instance {
    @SuppressWarnings("unused")
    void method(Object o) {}

    @SuppressWarnings("unused")
    void otherMethod(Object o) {}
  }
}
