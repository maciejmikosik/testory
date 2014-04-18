package org.testory.util.any;

import static java.util.Arrays.asList;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.arrayOf;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.common.Objects.print;
import static org.testory.util.any.Anyvocation.isVarargs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.common.Matchers.ProxyMatcher;
import org.testory.proxy.Invocation;

public class Matcherizes {
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
      Matcher matcher = !anysQueue.isEmpty() && mustBe(arguments.get(i), anysQueue.get(0))
          ? matcherize(anysQueue.remove(0))
          : matcherize(arguments.get(i));
      matchers.add(matcher);
    }
    return matchers;
  }

  private static Matcher matcherize(final Object argument) {
    return new ProxyMatcher(equalDeep(argument)) {
      public String toString() {
        return print(argument);
      }
    };
  }

  private static Matcher matcherize(final Any any) {
    return new ProxyMatcher(any.matcher) {
      public String toString() {
        return any.matcher == Matchers.anything
            ? "any(" + any.type.getName() + ")"
            : "any(" + any.type.getName() + ", " + any.matcher + ")";
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

  private static List<Object> unfold(List<?> packed) {
    ArrayList<Object> unpacked = new ArrayList<Object>();
    unpacked.addAll(packed.subList(0, packed.size() - 1));
    unpacked.addAll(asList((Object[]) last(packed)));
    return unpacked;
  }

  private static List<Matcher> fold(int length, List<Matcher> unpacked) {
    List<Matcher> packed = new ArrayList<Matcher>();
    packed.addAll(unpacked.subList(0, length - 1));
    packed.add(arrayOf(unpacked.subList(length - 1, unpacked.size())));
    return packed;
  }

  private static boolean mustBe(Object argument, Any any) {
    return any.token == argument;
  }
}
