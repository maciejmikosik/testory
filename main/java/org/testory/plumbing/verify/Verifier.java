package org.testory.plumbing.verify;

import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.VerifyingInOrder.verifyInOrder;
import static org.testory.plumbing.format.Body.body;
import static org.testory.plumbing.format.Header.header;
import static org.testory.plumbing.format.Multiline.multiline;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.implementing;

import java.lang.reflect.Method;

import org.testory.common.Matcher;
import org.testory.common.Optional;
import org.testory.common.PageFormatter;
import org.testory.plumbing.VerifyingInOrder;
import org.testory.plumbing.facade.Facade;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.wildcard.WildcardSupport;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;
import org.testory.proxy.extra.Overrider;

public class Verifier {
  private final Overrider overrider;
  private final PageFormatter pageFormatter;
  private final WildcardSupport wildcardSupport;
  private final History history;
  private final FilteredHistory<Invocation> invocationHistory;

  private Verifier(
      Overrider overrider,
      PageFormatter pageFormatter,
      WildcardSupport wildcardSupport,
      History history,
      FilteredHistory<Invocation> invocationHistory) {
    this.overrider = overrider;
    this.pageFormatter = pageFormatter;
    this.wildcardSupport = wildcardSupport;
    this.history = history;
    this.invocationHistory = invocationHistory;
  }

  public static Facade verifier(
      Proxer proxer,
      Overrider overrider,
      PageFormatter pageFormatter,
      WildcardSupport wildcardSupport,
      History history) {
    check(proxer != null);
    check(overrider != null);
    check(pageFormatter != null);
    check(wildcardSupport != null);
    check(history != null);
    final Verifier verifier = new Verifier(
        overrider,
        pageFormatter,
        wildcardSupport,
        history,
        filter(Invocation.class, history));
    return (Facade) proxer.proxy(implementing(Facade.class), new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        check(invocation.method.getName().startsWith("thenCalled"));
        Method method = Verifier.class.getMethod(
            invocation.method.getName(),
            invocation.method.getParameterTypes());
        return invocation(method, verifier, invocation.arguments).invoke();
      }
    });
  }

  public <T> T thenCalled(T mock) {
    return thenCalledTimes(exactly(1), mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(1), invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    return thenCalledTimes(exactly(0), mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(0), invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    return thenCalledTimes(exactly(number), mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(number), invocationMatcher);
  }

  public <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    return overrider.override(mock, new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledTimes(numberMatcher, wildcardSupport.matcherize(invocation));
        return defaultValue(invocation.method.getReturnType());
      }
    });
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    int numberOfCalls = 0;
    for (Invocation invocation : invocationHistory.get()) {
      if (invocationMatcher.matches(invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = asMatcher(numberMatcher).matches(numberOfCalls);
    if (!expected) {
      throw assertionError(pageFormatter
          .add(header("expected called times " + numberMatcher))
          .add(body(invocationMatcher))
          .add(header("but called"))
          .add(body("times " + numberOfCalls))
          .add(header("actual invocations"))
          .add(invocationHistory.get().size() > 0
              ? multiline(invocationHistory.get().reverse())
              : body("none"))
          .build());
    }
  }

  public <T> T thenCalledInOrder(T mock) {
    return overrider.override(mock, new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledInOrder(wildcardSupport.matcherize(invocation));
        return defaultValue(invocation.method.getReturnType());
      }
    });
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    Optional<VerifyingInOrder> verified = verifyInOrder(invocationMatcher, history.get());
    if (verified.isPresent()) {
      history.add(verified.get());
    } else {
      throw assertionError(pageFormatter
          .add(header("expected called in order"))
          .add(body(invocationMatcher))
          .add(header("but not called"))
          .add(header("actual invocations"))
          .add(invocationHistory.get().size() > 0
              ? multiline(invocationHistory.get().reverse())
              : body("none"))
          .build());
    }
  }

  private static Matcher exactly(final int number) {
    return new Matcher() {
      public boolean matches(Object item) {
        return item.equals(number);
      }

      public String toString() {
        return "" + number;
      }
    };
  }
}
