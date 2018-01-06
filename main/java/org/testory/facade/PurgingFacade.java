package org.testory.facade;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.implementing;

import org.testory.common.Chain;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;

public class PurgingFacade {
  private enum Word {
    GIVEN, WHEN, THEN
  }

  public static Facade purging(final History history, Proxer proxer, final Facade facade) {
    check(history != null);
    check(proxer != null);
    check(facade != null);
    return (Facade) proxer.proxy(implementing(Facade.class), new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        String methodName = invocation.method.getName();
        if (methodName.startsWith("given")) {
          given();
        } else if (methodName.startsWith("when")) {
          when();
        } else if (methodName.startsWith("then")) {
          then();
        }
        return invocation(invocation.method, facade, invocation.arguments).invoke();
      }

      private Word lastWord = Word.GIVEN;
      private Chain<Object> events = chain();

      private void given() {
        if (lastWord != Word.GIVEN) {
          history.cut(events);
        }
        lastWord = Word.GIVEN;
        events = history.get();
      }

      private void when() {
        if (lastWord != Word.GIVEN) {
          history.cut(events);
        }
        lastWord = Word.WHEN;
        events = history.get();
      }

      private void then() {
        lastWord = Word.THEN;
        events = history.get();
      }
    });
  }
}
