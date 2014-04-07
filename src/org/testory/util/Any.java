package org.testory.util;

import static java.util.Collections.nCopies;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.tryWrap;
import static org.testory.common.Collections.last;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.util.Matchers.invocationMatcher;
import static org.testory.util.Uniques.unique;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.proxy.Invocation;

public class Any {
  private final Class<?> type;
  private final Matcher matcher;
  private final Object token;

  private Any(Class<?> type, Matcher matcher, Object token) {
    this.type = type;
    this.matcher = matcher;
    this.token = token;
  }

  public static Any any(Class<?> type, Matcher matcher) {
    checkNotNull(type);
    checkNotNull(matcher);
    return new Any(type, matcher, unique(tryWrap(type)));
  }

  public boolean mustBe(Object argument) {
    return token == argument;
  }

  public boolean couldBe(Object argument) {
    return true;
  }

  public Object token() {
    return token;
  }

  public static Matcher solveInvocationMatcher(List<Any> anys, Invocation invocation) {
    boolean repacking = invocation.method.isVarArgs()
        && !last(anys).mustBe(last(invocation.arguments));
    List<Object> arguments = repacking
        ? unpackVarargs(invocation.arguments)
        : invocation.arguments;
    List<Boolean> solution = solve(anys, arguments);
    List<Matcher> matchers = matcherize(solution, anys, arguments);
    List<Matcher> argumentsMatchers = repacking
        ? packVarargs(invocation.method.getParameterTypes().length, matchers)
        : matchers;
    return invocationMatcher(invocation.method, invocation.instance, argumentsMatchers);
  }

  private static List<Boolean> solve(List<Any> anys, List<Object> arguments) {
    List<Boolean> solution = trySolveEager(anys, arguments);
    checkArgument(solution != null);
    checkArgument(areEqualDeep(reverse(solution), trySolveEager(reverse(anys), reverse(arguments))));
    return solution;
  }

  private static List<Object> unpackVarargs(List<?> packed) {
    ArrayList<Object> unpacked = new ArrayList<Object>();
    unpacked.addAll(packed.subList(0, packed.size() - 1));
    unpacked.addAll(Arrays.asList((Object[]) packed.get(packed.size() - 1)));
    return unpacked;
  }

  private static List<Matcher> packVarargs(int length, List<Matcher> unpacked) {
    List<Matcher> packed = new ArrayList<Matcher>();
    packed.addAll(unpacked.subList(0, length - 1));
    packed.add(arrayContainingInOrder(unpacked.subList(length - 1, unpacked.size())));
    return packed;
  }

  private static Matcher arrayContainingInOrder(final List<Matcher> elements) {
    return new Matcher() {
      public boolean matches(Object uncastItem) {
        Object[] item = (Object[]) uncastItem;
        if (item.length != elements.size()) {
          return false;
        }
        for (int i = 0; i < elements.size(); i++) {
          if (!elements.get(i).matches(item[i])) {
            return false;
          }
        }
        return true;
      }
    };
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
