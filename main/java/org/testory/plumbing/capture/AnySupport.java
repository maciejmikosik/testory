package org.testory.plumbing.capture;

import static java.lang.String.format;
import static org.testory.common.Classes.tryWrap;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Matchers.same;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.capture.Anyvocation.anyvocation;
import static org.testory.plumbing.capture.CollectingAny.collectingAny;
import static org.testory.plumbing.capture.ConsumingAny.consumingAny;
import static org.testory.plumbing.capture.MatcherizeAnyvocation.matcherize;
import static org.testory.plumbing.capture.Uniques.unique;

import java.util.ArrayList;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.plumbing.history.History;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class AnySupport {
  private final History history;
  private final Repairer repairer;

  private AnySupport(History history, Repairer repairer) {
    this.history = history;
    this.repairer = repairer;
  }

  public static AnySupport anySupport(History history, Repairer repairer) {
    check(history != null);
    return new AnySupport(history, repairer);
  }

  public Capturer getCapturer() {
    return new Capturer() {
      public InvocationMatcher capture(Invocation invocation) {
        check(invocation != null);
        List<CollectingAny> anys = consumeAnys();
        return matcherize(repairer.repair(
            anyvocation(invocation.method, invocation.instance, invocation.arguments, anys)));
      }
    };
  }

  private List<CollectingAny> consumeAnys() {
    List<CollectingAny> anys = new ArrayList<CollectingAny>();
    for (Object event : history.get()) {
      if (event instanceof CollectingAny) {
        anys.add(0, (CollectingAny) event);
      } else if (event instanceof ConsumingAny) {
        break;
      }
    }
    history.add(consumingAny());
    return anys;
  }

  public Object any(final Class<?> type) {
    return anyImpl(Matchers.anything, type, new Object() {
      public String toString() {
        return format("any(%s)", type.getName());
      }
    });
  }

  public Object any(final Class<?> type, Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    final Matcher asMatcher = asMatcher(matcher);
    return anyImpl(asMatcher, type, new Object() {
      public String toString() {
        return format("any(%s, %s)", type.getName(), asMatcher);
      }
    });
  }

  public Object a(final Object value) {
    check(value != null);
    return anyImpl(equalDeep(value), value.getClass(), new Object() {
      public String toString() {
        return format("a(%s)", value);
      }
    });
  }

  public Object the(final Object value) {
    check(value != null);
    return anyImpl(same(value), value.getClass(), new Object() {
      public String toString() {
        return format("the(%s)", value);
      }
    });
  }

  private Object anyImpl(Matcher matcher, Class<?> type, Object printable) {
    Object token = unique(tryWrap(type));
    history.add(collectingAny(matcher, token, printable));
    return token;
  }
}
