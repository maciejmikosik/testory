package org.testory.plumbing.wildcard;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.common.SequenceFormatter.sequence;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Formatter;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.common.SequenceFormatter;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class Matcherizer {
  private final Formatter formatter;
  private final SequenceFormatter sequenceFormatter;

  private Matcherizer(Formatter formatter) {
    this.formatter = formatter;
    this.sequenceFormatter = sequence(", ", formatter);
  }

  public static Matcherizer matcherizer(Formatter formatter) {
    check(formatter != null);
    return new Matcherizer(formatter);
  }

  public InvocationMatcher matcherize(WildcardInvocation invocation) {
    List<Object> arguments = invocation.mayBeFolded()
        ? unfold(invocation.arguments)
        : invocation.arguments;
    List<Matcher> matchers = matcherize(invocation.wildcards, arguments);
    List<Matcher> argumentsMatchers = invocation.mayBeFolded()
        ? fold(invocation.method.getParameterTypes().length, matchers)
        : matchers;
    return matcherize(invocation.method, invocation.instance, argumentsMatchers);
  }

  private List<Matcher> fold(int length, List<Matcher> unfolded) {
    List<Matcher> folded = new ArrayList<>();
    folded.addAll(unfolded.subList(0, length - 1));
    folded.add(arrayOf(unfolded.subList(length - 1, unfolded.size())));
    return folded;
  }

  private static List<Object> unfold(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asList((Object[]) last(folded)));
    return unfolded;
  }

  private List<Matcher> matcherize(List<Wildcard> wildcards, List<Object> arguments) {
    List<Wildcard> wildcardQueue = new ArrayList<>(wildcards);
    List<Matcher> matchers = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      Matcher matcher = !wildcardQueue.isEmpty() && wildcardQueue.get(0).token == arguments.get(i)
          ? wildcardQueue.remove(0).matcher
          : matcherize(arguments.get(i));
      matchers.add(matcher);
    }
    return matchers;
  }

  private Matcher matcherize(final Object argument) {
    return new DelegatingMatcher(equalDeep(argument)) {
      public String toString() {
        return formatter.format(argument);
      }
    };
  }

  private InvocationMatcher matcherize(final Method method, final Object instance,
      final List<Matcher> arguments) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.method.equals(method)
            && invocation.instance == instance
            && listOf(arguments).matches(invocation.arguments);
      }

      public String toString() {
        return format("%s.%s(%s)", instance, method.getName(), sequenceFormatter.format(arguments));
      }
    };
  }

  private Matcher arrayOf(final List<Matcher> elements) {
    return new DelegatingMatcher(Matchers.arrayOf(elements)) {
      public String toString() {
        return format("[%s]", sequenceFormatter.format(elements));
      }
    };
  }
}
