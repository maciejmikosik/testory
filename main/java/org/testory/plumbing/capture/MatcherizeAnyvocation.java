package org.testory.plumbing.capture;

import static java.util.Arrays.asList;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.common.Objects.print;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class MatcherizeAnyvocation {
  public static InvocationMatcher matcherize(Anyvocation anyvocation) {
    List<Object> arguments = anyvocation.mayBeFolded()
        ? unfold(anyvocation.arguments)
        : anyvocation.arguments;
    List<Matcher> matchers = matcherize(anyvocation.anys, arguments);
    List<Matcher> argumentsMatchers = anyvocation.mayBeFolded()
        ? fold(anyvocation.method.getParameterTypes().length, matchers)
        : matchers;
    return matcherize(anyvocation.method, anyvocation.instance, argumentsMatchers);
  }

  private static List<Matcher> fold(int length, List<Matcher> unfolded) {
    List<Matcher> folded = new ArrayList<Matcher>();
    folded.addAll(unfolded.subList(0, length - 1));
    folded.add(arrayOf(unfolded.subList(length - 1, unfolded.size())));
    return folded;
  }

  private static List<Object> unfold(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<Object>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asList((Object[]) last(folded)));
    return unfolded;
  }

  private static List<Matcher> matcherize(List<CollectingAny> anys, List<Object> arguments) {
    List<CollectingAny> anysQueue = new ArrayList<CollectingAny>(anys);
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
    return new DelegatingMatcher(equalDeep(argument)) {
      public String toString() {
        return print(argument);
      }
    };
  }

  private static Matcher matcherize(final CollectingAny any) {
    return new DelegatingMatcher(any.matcher) {
      public String toString() {
        return any.printable.toString();
      }
    };
  }

  private static InvocationMatcher matcherize(final Method method, final Object instance,
      final List<Matcher> arguments) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.method.equals(method)
            && invocation.instance == instance
            && listOf(arguments).matches(invocation.arguments);
      }

      public String toString() {
        return instance + "." + method.getName() + "(" + join(", ", arguments) + ")";
      }
    };
  }

  private static Matcher arrayOf(final List<Matcher> elements) {
    return new DelegatingMatcher(Matchers.arrayOf(elements)) {
      public String toString() {
        return "[" + join(", ", elements) + "]";
      }
    };
  }
}
