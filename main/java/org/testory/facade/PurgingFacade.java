package org.testory.facade;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.implementing;

import java.lang.reflect.Method;

import org.testory.common.Chain;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;

public class PurgingFacade {
  public static Facade purging(final History history, Proxer proxer, final Facade facade) {
    check(history != null);
    check(proxer != null);
    check(facade != null);
    return (Facade) proxer.proxy(implementing(Facade.class), new Handler() {
      private Word lastWord = Word.GIVEN;
      private Chain<Object> lastEvents = chain();

      public Object handle(Invocation invocation) throws Throwable {
        Word word = wordFor(invocation.method);
        if (word != null) {
          if (requiresPurge(lastWord, word)) {
            purge();
          }
          lastWord = word;
          lastEvents = history.get();
        }
        return invocation(invocation.method, facade, invocation.arguments).invoke();
      }

      private void purge() {
        Chain<Object> chain = history.get();
        while (chain.size() > 0) {
          if (chain == lastEvents || chain.get() instanceof Invocation) {
            history.cut(chain);
            break;
          }
          chain = chain.remove();
        }
      }
    });
  }

  private static Word wordFor(Method method) {
    String methodName = method.getName();
    if (methodName.startsWith("given")) {
      return Word.GIVEN;
    } else if (methodName.startsWith("when")) {
      return Word.WHEN;
    } else if (methodName.startsWith("then")) {
      return Word.THEN;
    } else {
      return null;
    }
  }

  private static boolean requiresPurge(Word previous, Word next) {
    return previous != Word.GIVEN && next != Word.THEN;
  }

  private enum Word {
    GIVEN, WHEN, THEN
  }
}
