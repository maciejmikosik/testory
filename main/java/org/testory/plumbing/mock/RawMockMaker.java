package org.testory.plumbing.mock;

import static org.testory.plumbing.Mocking.mocking;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.proxy.Typing.subclassing;

import org.testory.plumbing.Maker;
import org.testory.plumbing.PlumbingException;
import org.testory.plumbing.Stubbing;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;

public class RawMockMaker implements Maker {
  private final History history;
  private final FilteredHistory<Stubbing> stubbingHistory;
  private final Proxer proxer;

  private RawMockMaker(History history, FilteredHistory<Stubbing> stubbingHistory, Proxer proxer) {
    this.history = history;
    this.stubbingHistory = stubbingHistory;
    this.proxer = proxer;
  }

  public static Maker rawMockMaker(History history, Proxer proxer) {
    check(history != null);
    check(proxer != null);
    FilteredHistory<Stubbing> stubbingHistory = filter(Stubbing.class, history);
    return new RawMockMaker(history, stubbingHistory, proxer);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    Object mock = proxer.proxy(subclassing(type), handler());
    history.add(mocking(mock, name));
    return (T) mock;
  }

  private Handler handler() {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        history.add(invocation);
        Stubbing stubbing = findStubbingFor(invocation);
        return stubbing.handler.handle(invocation);
      }
    };
  }

  public Stubbing findStubbingFor(Invocation invocation) {
    for (Stubbing stubbing : stubbingHistory.get()) {
      if (stubbing.invocationMatcher.matches(invocation)) {
        return stubbing;
      }
    }
    throw new PlumbingException();
  }
}
