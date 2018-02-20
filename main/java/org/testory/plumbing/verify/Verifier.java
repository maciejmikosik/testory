package org.testory.plumbing.verify;

import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.common.Chain.chain;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.format.Body.body;
import static org.testory.plumbing.format.Header.header;
import static org.testory.plumbing.format.Multiline.multiline;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.verify.Verified.verified;
import static org.testory.proxy.Typing.implementing;

import org.testory.common.Chain;
import org.testory.common.Matcher;
import org.testory.common.PageFormatter;
import org.testory.plumbing.facade.Facade;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.wildcard.Wildcarder;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;
import org.testory.proxy.extra.Overrider;

public class Verifier {
  public static Facade verifier(
      Proxer proxer,
      final Overrider overrider,
      final PageFormatter pageFormatter,
      final Wildcarder wildcarder,
      final History history) {
    check(proxer != null);
    check(overrider != null);
    check(pageFormatter != null);
    check(wildcarder != null);
    check(history != null);
    final FilteredHistory<Invocation> invocationHistory = filter(Invocation.class, history);
    return (Facade) proxer.proxy(implementing(Facade.class), new Handler() {
      public Object handle(final Invocation thenCalledInvocation) throws Throwable {
        check(thenCalledInvocation.method.getName().startsWith("thenCalled"));
        if (last(thenCalledInvocation.method.getParameterTypes()) == InvocationMatcher.class) {
          verify(thenCalledInvocation, (InvocationMatcher) last(thenCalledInvocation.arguments));
          return null;
        } else {
          return overrider.override(last(thenCalledInvocation.arguments), new Handler() {
            public Object handle(Invocation invocation) {
              verify(thenCalledInvocation, wildcarder.matcherize(invocation));
              return defaultValue(invocation.method.getReturnType());
            }
          });
        }
      }

      private void verify(Invocation thenCalledInvocation, InvocationMatcher invocationMatcher) {
        if (thenCalledInvocation.method.getName().endsWith("InOrder")) {
          thenCalledInOrder(invocationMatcher);
        } else {
          thenCalledTimes(numberMatcher(thenCalledInvocation), invocationMatcher);
        }
      }

      private void thenCalledTimes(Matcher numberMatcher, InvocationMatcher invocationMatcher) {
        int numberOfCalls = 0;
        for (Invocation invocation : invocationHistory.get()) {
          if (invocationMatcher.matches(invocation)) {
            numberOfCalls++;
          }
        }
        if (!numberMatcher.matches(numberOfCalls)) {
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

      private void thenCalledInOrder(InvocationMatcher invocationMatcher) {
        Chain<Object> remainingFlipped = chain();
        for (Object event : history.get()) {
          if (event instanceof Verified) {
            remainingFlipped = remainingFlipped.addAll(((Verified) event).remaining);
            break;
          } else {
            remainingFlipped = remainingFlipped.add(event);
          }
        }
        while (remainingFlipped.size() > 0) {
          Object event = remainingFlipped.get();
          remainingFlipped = remainingFlipped.remove();
          if (event instanceof Invocation && invocationMatcher.matches((Invocation) event)) {
            history.add(verified(remainingFlipped.reverse()));
            return;
          }
        }
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
    });
  }

  private static Matcher numberMatcher(Invocation invocation) {
    if (invocation.method.getName().equals("thenCalledNever")) {
      return numberMatcher(0);
    } else if (invocation.method.getName().equals("thenCalled")) {
      return numberMatcher(1);
    } else if (invocation.method.getName().equals("thenCalledTimes")
        && invocation.method.getParameterTypes()[0] == int.class) {
      return numberMatcher((int) invocation.arguments.get(0));
    } else {
      return asMatcher(invocation.arguments.get(0));
    }
  }

  private static Matcher numberMatcher(final int number) {
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
