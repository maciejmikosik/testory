package org.testory.plumbing.mock;

import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.mock.Mocked.mocked;
import static org.testory.proxy.Typing.subclassing;

import org.testory.plumbing.Maker;
import org.testory.plumbing.PlumbingException;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;

public class RawMockMaker implements Maker {
  private final History history;
  private final FilteredHistory<Stubbed> stubbedHistory;
  private final Proxer proxer;

  private RawMockMaker(History history, FilteredHistory<Stubbed> stubbedHistory, Proxer proxer) {
    this.history = history;
    this.stubbedHistory = stubbedHistory;
    this.proxer = proxer;
  }

  public static Maker rawMockMaker(History history, Proxer proxer) {
    check(history != null);
    check(proxer != null);
    FilteredHistory<Stubbed> stubbedHistory = filter(Stubbed.class, history);
    return new RawMockMaker(history, stubbedHistory, proxer);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    Object mock = proxer.proxy(subclassing(type), handler());
    history.add(mocked(mock, name));
    return (T) mock;
  }

  private Handler handler() {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        history.add(invocation);
        Stubbed stubbed = stubbed(invocation);
        return stubbed.handler.handle(invocation);
      }
    };
  }

  public Stubbed stubbed(Invocation invocation) {
    for (Stubbed stubbed : stubbedHistory.get()) {
      if (stubbed.invocationMatcher.matches(invocation)) {
        return stubbed;
      }
    }
    throw new PlumbingException();
  }
}
