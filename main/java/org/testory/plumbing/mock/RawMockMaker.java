package org.testory.plumbing.mock;

import static org.testory.plumbing.Calling.calling;
import static org.testory.plumbing.Mocking.mocking;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.proxy.Typing.typing;

import java.util.Arrays;
import java.util.HashSet;

import org.testory.plumbing.Maker;
import org.testory.plumbing.PlumbingException;
import org.testory.plumbing.Stubbing;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class RawMockMaker implements Maker {
  private final Proxer proxer;
  private final History history;
  private final FilteredHistory<Stubbing> stubbingHistory;

  private RawMockMaker(Proxer proxer, History history, FilteredHistory<Stubbing> stubbingHistory) {
    this.proxer = proxer;
    this.history = history;
    this.stubbingHistory = stubbingHistory;
  }

  public static Maker rawMockMaker(Proxer proxer, History history) {
    check(proxer != null);
    check(history != null);
    FilteredHistory<Stubbing> stubbingHistory = filter(Stubbing.class, history);
    return new RawMockMaker(proxer, history, stubbingHistory);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    Object mock = proxer.proxy(typingFor(type), handler());
    history.add(mocking(mock, name));
    return (T) mock;
  }

  private static <T> Typing typingFor(Class<T> type) {
    return type.isInterface()
        ? typing(Object.class, new HashSet<>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
  }

  private Handler handler() {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        history.add(calling(invocation));
        Stubbing stubbing = stubbingFor(invocation);
        return stubbing.handler.handle(invocation);
      }
    };
  }

  public Stubbing stubbingFor(Invocation invocation) {
    for (Stubbing stubbing : stubbingHistory.get()) {
      if (stubbing.invocationMatcher.matches(invocation)) {
        return stubbing;
      }
    }
    throw new PlumbingException();
  }
}
