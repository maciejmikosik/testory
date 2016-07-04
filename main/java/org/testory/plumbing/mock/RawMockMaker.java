package org.testory.plumbing.mock;

import static org.testory.TestoryException.check;
import static org.testory.plumbing.Calling.calling;
import static org.testory.plumbing.Mocking.isMock;
import static org.testory.plumbing.Mocking.mocking;
import static org.testory.plumbing.Stubbing.findStubbing;
import static org.testory.plumbing.Stubbing.isStubbed;
import static org.testory.proxy.Typing.typing;

import java.util.Arrays;
import java.util.HashSet;

import org.testory.common.Chain;
import org.testory.plumbing.History;
import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class RawMockMaker {
  public static Maker rawMockMaker(final Proxer proxer, final History history) {
    check(proxer != null);
    check(history != null);
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        check(type != null);
        check(name != null);
        Typing typing = typingFor(type);
        Handler handler = handler(history);
        Object mock = proxer.proxy(typing, handler);
        history.add(mocking(mock, name));
        return (T) mock;
      }
    };
  }

  private static <T> Typing typingFor(Class<T> type) {
    return type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
  }

  private static Handler handler(final History history) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Chain<Object> events = history.get();
        check(isMock(invocation.instance, events));
        check(isStubbed(invocation, events));
        history.add(calling(invocation));
        Stubbing stubbing = findStubbing(invocation, events).get();
        return stubbing.handler.handle(invocation);
      }
    };
  }
}
