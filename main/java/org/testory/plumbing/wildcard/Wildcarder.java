package org.testory.plumbing.wildcard;

import static java.lang.String.format;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Matchers.same;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.wildcard.Consumed.consumed;
import static org.testory.plumbing.wildcard.Wildcard.wildcard;
import static org.testory.plumbing.wildcard.WildcardInvocation.wildcardInvocation;

import java.util.ArrayList;
import java.util.List;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Formatter;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.plumbing.history.History;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class Wildcarder {
  private final History history;
  private final Tokenizer tokenizer;
  private final Repairer repairer;
  private final Matcherizer matcherizer;
  private final Formatter formatter;

  private Wildcarder(
      History history,
      Tokenizer tokenizer,
      Repairer repairer,
      Matcherizer matcherizer,
      Formatter formatter) {
    this.history = history;
    this.tokenizer = tokenizer;
    this.repairer = repairer;
    this.matcherizer = matcherizer;
    this.formatter = formatter;
  }

  public static Wildcarder wildcarder(
      History history,
      Tokenizer tokenizer,
      Repairer repairer,
      Matcherizer matcherizer,
      Formatter formatter) {
    check(history != null);
    check(tokenizer != null);
    check(repairer != null);
    check(matcherizer != null);
    check(formatter != null);
    return new Wildcarder(history, tokenizer, repairer, matcherizer, formatter);
  }

  public Object any(final Class<?> type) {
    check(type != null);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(Matchers.anything) {
      public String toString() {
        return format("any(%s)", type.getName());
      }
    };
    return anyImpl(printableMatcher, type);
  }

  public Object any(final Class<?> type, Object matcher) {
    check(type != null);
    check(matcher != null);
    check(isMatcher(matcher));
    final Matcher asMatcher = asMatcher(matcher);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(asMatcher) {
      public String toString() {
        return format("any(%s, %s)", type.getName(), asMatcher);
      }
    };
    return anyImpl(printableMatcher, type);
  }

  public Object anyInstanceOf(final Class<?> type) {
    check(type != null);
    Matcher matcher = new Matcher() {
      public boolean matches(Object item) {
        return type.isInstance(item);
      }

      public String toString() {
        return format("anyInstanceOf(%s)", type.getName());
      }
    };
    return anyImpl(matcher, type);
  }

  public Object a(final Object value) {
    check(value != null);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(equalDeep(value)) {
      public String toString() {
        return format("a(%s)", formatter.format(value));
      }
    };
    return anyImpl(printableMatcher, value.getClass());
  }

  public Object the(final Object value) {
    check(value != null);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(same(value)) {
      public String toString() {
        return format("the(%s)", formatter.format(value));
      }
    };
    return anyImpl(printableMatcher, value.getClass());
  }

  private Object anyImpl(Matcher matcher, Class<?> type) {
    Object token = tokenizer.token(type);
    history.add(wildcard(matcher, token));
    return token;
  }

  public InvocationMatcher matcherize(Invocation invocation) {
    check(invocation != null);
    List<Wildcard> wildcards = consume();
    WildcardInvocation wildcardInvocation = wildcardInvocation(
        invocation.method,
        invocation.instance,
        invocation.arguments,
        wildcards);
    return matcherizer.matcherize(repairer.repair(wildcardInvocation));
  }

  private List<Wildcard> consume() {
    List<Wildcard> wildcards = new ArrayList<>();
    for (Object event : history.get()) {
      if (event instanceof Wildcard) {
        wildcards.add(0, (Wildcard) event);
      } else if (event instanceof Consumed) {
        break;
      }
    }
    history.add(consumed());
    return wildcards;
  }
}
