package org.testory.plumbing.capture;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Objects.deepEquals;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Collections.flip;
import static org.testory.common.Collections.immutable;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.common.Objects.print;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.common.Matchers.MatcherDecorator;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.proxy.Invocation;

public class Anyvocation {
  public final Method method;
  public final Object instance;
  public final List<Object> arguments;
  public final List<Any> anys;

  private Anyvocation(Method method, Object instance, List<Object> arguments, List<Any> anys) {
    this.method = method;
    this.instance = instance;
    this.arguments = arguments;
    this.anys = anys;
  }

  public static Anyvocation anyvocation(Method method, Object instance, List<Object> arguments,
      List<Any> anys) {
    check(method != null);
    check(instance != null);
    check(arguments != null);
    check(anys != null);
    return new Anyvocation(method, instance, immutable(arguments), immutable(anys));
  }

  private static boolean isVarargs(Anyvocation anyvocation) {
    return anyvocation.method.isVarArgs() && !anyvocation.anys.isEmpty()
        && last(anyvocation.anys).token != last(anyvocation.arguments);
  }

  public static Optional<Anyvocation> repair(Anyvocation anyvocation) {
    check(anyvocation != null);
    boolean isVarargs = isVarargs(anyvocation);
    List<Class<?>> parameters = asList(anyvocation.method.getParameterTypes());
    List<Object> unfolded = isVarargs
        ? unfoldArguments(anyvocation.arguments)
        : anyvocation.arguments;
    List<Class<?>> unfoldedParameters = isVarargs
        ? unfoldParameters(unfolded.size(), parameters)
        : parameters;
    List<Object> repairedUnfolded = repair(unfoldedParameters, unfolded, anyvocation);
    if (repairedUnfolded == null) {
      return Optional.empty();
    }
    List<Object> repaired = isVarargs
        ? foldArguments(parameters.size(), repairedUnfolded)
        : repairedUnfolded;
    return Optional.of(anyvocation(anyvocation.method, anyvocation.instance, repaired,
        anyvocation.anys));
  }

  @Nullable
  private static List<Object> repair(List<Class<?>> parameters, List<Object> arguments,
      Anyvocation anyvocation) {
    List<Boolean> solution = trySolveEager(anyvocation.anys, parameters, arguments);
    if (solution == null) {
      return null;
    }
    if (!deepEquals(flip(solution),
        trySolveEager(flip(anyvocation.anys), flip(parameters), flip(arguments)))) {
      return null;
    }
    List<Any> anysQueue = new ArrayList<Any>(anyvocation.anys);
    List<Object> repaired = new ArrayList<Object>();
    for (int i = 0; i < solution.size(); i++) {
      repaired.add(solution.get(i)
          ? anysQueue.remove(0).token
          : arguments.get(i));
    }
    return repaired;
  }

  @Nullable
  private static List<Boolean> trySolveEager(List<Any> anys, List<Class<?>> parameters,
      List<Object> arguments) {
    List<Boolean> solution = new ArrayList<Boolean>(nCopies(arguments.size(), false));
    int nextIndex = 0;
    nextAny: for (Any any : anys) {
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (any.token == arguments.get(i)) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (parameters.get(i).isPrimitive()) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      return null;
    }
    return solution;
  }

  private static List<Class<?>> unfoldParameters(int size, List<Class<?>> parameters) {
    List<Class<?>> unfolded = new ArrayList<Class<?>>();
    unfolded.addAll(parameters.subList(0, parameters.size() - 1));
    unfolded.addAll(nCopies(size - (parameters.size() - 1), last(parameters).getComponentType()));
    return unfolded;
  }

  private static List<Object> unfoldArguments(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<Object>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asBoxingList(last(folded)));
    return unfolded;
  }

  private static List<Object> asBoxingList(Object array) {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < Array.getLength(array); i++) {
      list.add(Array.get(array, i));
    }
    return list;
  }

  private static List<Object> foldArguments(int length, List<Object> arguments) {
    List<Object> folded = new ArrayList<Object>();
    folded.addAll(arguments.subList(0, length - 1));
    folded.add(asArray(arguments.subList(length - 1, arguments.size())));
    return folded;
  }

  private static Object asArray(List<Object> elements) {
    Object array = Array.newInstance(Object.class, 0);
    return elements.toArray((Object[]) array);
  }

  public static Matcher matcherize(Anyvocation anyvocation) {
    boolean isVarargs = isVarargs(anyvocation);
    List<Object> arguments = isVarargs
        ? unfold(anyvocation.arguments)
        : anyvocation.arguments;
    List<Matcher> matchers = matcherize(anyvocation.anys, arguments);
    List<Matcher> argumentsMatchers = isVarargs
        ? fold(anyvocation.method.getParameterTypes().length, matchers)
        : matchers;
    return matcherize(anyvocation.method, anyvocation.instance, argumentsMatchers);
  }

  private static List<Matcher> matcherize(List<Any> anys, List<Object> arguments) {
    List<Any> anysQueue = new ArrayList<Any>(anys);
    List<Matcher> matchers = new ArrayList<Matcher>();
    for (int i = 0; i < arguments.size(); i++) {
      Matcher matcher = !anysQueue.isEmpty() && anysQueue.get(0).token == arguments.get(i)
          ? matcherize(anysQueue.remove(0))
          : matcherize(arguments.get(i));
      matchers.add(matcher);
    }
    return matchers;
  }

  private static Matcher matcherize(final Object argument) {
    return new MatcherDecorator(equalDeep(argument)) {
      public String toString() {
        return print(argument);
      }
    };
  }

  private static Matcher matcherize(final Any any) {
    return new MatcherDecorator(any.matcher) {
      public String toString() {
        return any.toString();
      }
    };
  }

  private static Matcher matcherize(final Method method, final Object instance,
      final List<Matcher> arguments) {
    return new Matcher() {
      public boolean matches(Object item) {
        Invocation invocation = (Invocation) item;
        return invocation.method.equals(method) && invocation.instance == instance
            && listOf(arguments).matches(invocation.arguments);
      }

      public String toString() {
        return instance + "." + method.getName() + "(" + join(", ", arguments) + ")";
      }
    };
  }

  private static List<Object> unfold(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<Object>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asList((Object[]) last(folded)));
    return unfolded;
  }

  private static List<Matcher> fold(int length, List<Matcher> unfolded) {
    List<Matcher> folded = new ArrayList<Matcher>();
    folded.addAll(unfolded.subList(0, length - 1));
    folded.add(arrayOf(unfolded.subList(length - 1, unfolded.size())));
    return folded;
  }

  private static Matcher arrayOf(final List<Matcher> elements) {
    return new MatcherDecorator(Matchers.arrayOf(elements)) {
      public String toString() {
        return "[" + join(", ", elements) + "]";
      }
    };
  }
}
