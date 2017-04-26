package org.testory.plumbing.im.wildcard;

import static java.lang.String.format;
import static org.testory.common.Classes.tryWrap;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Matchers.same;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.im.wildcard.Uniques.unique;
import static org.testory.plumbing.im.wildcard.Wildcard.wildcard;

import org.testory.common.DelegatingMatcher;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.plumbing.history.History;

public class WildcardSupport {
  private final History history;

  private WildcardSupport(History history) {
    this.history = history;
  }

  public static WildcardSupport wildcardSupport(History history) {
    check(history != null);
    return new WildcardSupport(history);
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
