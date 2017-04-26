package org.testory.plumbing.im.wildcard;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.im.wildcard.WildcardInvocation.wildcardInvocation;
import static org.testory.plumbing.im.wildcard.Wildcarded.wildcarded;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Formatter;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.Matcherizer;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class WildcardMatcherizer implements Matcherizer {
  private final Repairer repairer;
  private final History history;
  private final Formatter formatter;

  private WildcardMatcherizer(History history, Repairer repairer, Formatter formatter) {
    this.history = history;
    this.repairer = repairer;
    this.formatter = formatter;
  }

  public static Matcherizer wildcardMatcherizer(
      History history,
      Repairer repairer,
      Formatter formatter) {
    check(history != null);
    check(repairer != null);
    check(formatter != null);
    return new WildcardMatcherizer(history, repairer, formatter);
  }

  public InvocationMatcher matcherize(Invocation invocation) {
    List<Wildcard> wildcards = consumeWildcards();
    WildcardInvocation wildcardInvocation = wildcardInvocation(
        invocation.method,
        invocation.instance,
        invocation.arguments,
        wildcards);
    return matcherize(repairer.repair(wildcardInvocation));
  }

  private List<Wildcard> consumeWildcards() {
    List<Wildcard> wildcards = new ArrayList<Wildcard>();
    for (Object event : history.get()) {
      if (event instanceof Wildcard) {
        wildcards.add(0, (Wildcard) event);
      } else if (event instanceof Wildcarded) {
        break;
      }
    }
    history.add(wildcarded());
    return wildcards;
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

  private List<Matcher> matcherize(List<Wildcard> wildcards, List<Object> arguments) {
    List<Wildcard> wildcardQueue = new ArrayList<Wildcard>(wildcards);
    List<Matcher> matchers = new ArrayList<Matcher>();
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
        return format("%s.%s(%s)", instance, method.getName(), formatter.formatSequence(arguments));
      }
    };
  }

  private Matcher arrayOf(final List<Matcher> elements) {
    return new DelegatingMatcher(Matchers.arrayOf(elements)) {
      public String toString() {
        return format("[%s]", formatter.formatSequence(elements));
      }
    };
  }
}
