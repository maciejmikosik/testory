package org.testory.plumbing.capture.wildcard;

import static java.lang.String.format;
import static org.testory.common.Classes.tryWrap;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Matchers.same;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.capture.Capturing.capturing;
import static org.testory.plumbing.capture.wildcard.MatcherizeWildcardInvocation.matcherize;
import static org.testory.plumbing.capture.wildcard.Uniques.unique;
import static org.testory.plumbing.capture.wildcard.Wildcard.wildcard;
import static org.testory.plumbing.capture.wildcard.WildcardInvocation.wildcardInvocation;

import java.util.ArrayList;
import java.util.List;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.plumbing.capture.Capturer;
import org.testory.plumbing.capture.Capturing;
import org.testory.plumbing.history.History;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class WildcardSupport {
  private final History history;
  private final Repairer repairer;

  private WildcardSupport(History history, Repairer repairer) {
    this.history = history;
    this.repairer = repairer;
  }

  public static WildcardSupport wildcardSupport(History history, Repairer repairer) {
    check(history != null);
    check(repairer != null);
    return new WildcardSupport(history, repairer);
  }

  public Capturer getCapturer() {
    return new Capturer() {
      public InvocationMatcher capture(Invocation invocation) {
        check(invocation != null);
        List<Wildcard> wildcards = consumeWildcards();
        return matcherize(repairer.repair(
            wildcardInvocation(invocation.method, invocation.instance, invocation.arguments, wildcards)));
      }
    };
  }

  private List<Wildcard> consumeWildcards() {
    List<Wildcard> wildcards = new ArrayList<Wildcard>();
    for (Object event : history.get()) {
      if (event instanceof Wildcard) {
        wildcards.add(0, (Wildcard) event);
      } else if (event instanceof Capturing) {
        break;
      }
    }
    history.add(capturing());
    return wildcards;
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

  public Object a(final Object value) {
    check(value != null);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(equalDeep(value)) {
      public String toString() {
        return format("a(%s)", value);
      }
    };
    return anyImpl(printableMatcher, value.getClass());
  }

  public Object the(final Object value) {
    check(value != null);
    DelegatingMatcher printableMatcher = new DelegatingMatcher(same(value)) {
      public String toString() {
        return format("the(%s)", value);
      }
    };
    return anyImpl(printableMatcher, value.getClass());
  }

  private Object anyImpl(Matcher matcher, Class<?> type) {
    Object token = unique(tryWrap(type));
    history.add(wildcard(matcher, token));
    return token;
  }
}
