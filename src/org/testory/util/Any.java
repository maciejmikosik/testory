package org.testory.util;

import static java.util.Collections.nCopies;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.zeroOrNull;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.util.Matchers.invocationMatcher;
import static org.testory.util.Uniques.hasUniques;
import static org.testory.util.Uniques.unique;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.proxy.Invocation;

public class Any {
  private final Class<?> type;
  private final Matcher matcher;
  private final boolean hasToken;
  private final Object tokenOrValue;

  private Any(Class<?> type, Matcher matcher, boolean hasToken, Object token) {
    this.type = type;
    this.matcher = matcher;
    this.hasToken = hasToken;
    this.tokenOrValue = token;
  }

  public static Any any(Class<?> type, Matcher matcher) {
    checkNotNull(type);
    checkNotNull(matcher);
    boolean hasToken = hasUniques(type);
    Object tokenOrValue = hasToken
        ? unique(type)
        : zeroOrNull(type);
    return new Any(type, matcher, hasToken, tokenOrValue);
  }

  public boolean mustBe(Object argument) {
    return hasToken && tokenOrValue == argument;
  }

  public boolean couldBe(Object argument) {
    return true;
  }

  public Object tokenOrValue() {
    return tokenOrValue;
  }

  public static Matcher solveInvocationMatcher(List<Any> anys, Invocation invocation) {
    List<Boolean> solution = trySolveEager(anys, invocation.arguments);
    checkArgument(solution != null);
    checkArgument(areEqualDeep(reverse(solution),
        trySolveEager(reverse(anys), reverse(invocation.arguments))));
    List<Matcher> matchers = matcherize(solution, anys, invocation.arguments);
    return invocationMatcher(invocation.method, invocation.instance, matchers);
  }

  private static List<Matcher> matcherize(List<Boolean> solution, List<Any> anys,
      List<Object> arguments) {
    List<Any> anysQueue = new ArrayList<Any>(anys);
    List<Matcher> matchers = new ArrayList<Matcher>();
    for (int i = 0; i < arguments.size(); i++) {
      Matcher matcher = solution.get(i)
          ? asMatcher(anysQueue.remove(0))
          : asMatcher(arguments.get(i));
      matchers.add(matcher);
    }
    return matchers;
  }

  @Nullable
  private static List<Boolean> trySolveEager(List<Any> anys, List<Object> arguments) {
    List<Boolean> solution = new ArrayList<Boolean>(nCopies(arguments.size(), false));
    int nextIndex = 0;
    nextAny: for (Any any : anys) {
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (any.mustBe(arguments.get(i))) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (any.couldBe(arguments.get(i))) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      return null;
    }
    return solution;
  }

  private static <E> List<E> reverse(final List<E> list) {
    return new AbstractList<E>() {
      public E get(int index) {
        return list.get(size() - 1 - index);
      }

      public int size() {
        return list.size();
      }
    };
  }

  private static Matcher asMatcher(final Object argument) {
    return new Matcher() {
      public boolean matches(Object item) {
        return areEqualDeep(argument, item);
      }

      public String toString() {
        return print(argument);
      }
    };
  }

  private static Matcher asMatcher(final Any any) {
    return new Matcher() {
      public boolean matches(Object item) {
        return any.matcher.matches(item);
      }

      public String toString() {
        return any.matcher == Matchers.anything
            ? "any(" + any.type.getName() + ")"
            : "any(" + any.type.getName() + ", " + any.matcher + ")";
      }
    };
  }
}
