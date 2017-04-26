package org.testory.plumbing.mock;

import static org.testory.common.Classes.defaultValue;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;

import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class NiceMockMaker implements Maker {
  private final Maker mockMaker;
  private final History history;

  private NiceMockMaker(Maker mockMaker, History history) {
    this.mockMaker = mockMaker;
    this.history = history;
  }

  public static Maker nice(Maker mockMaker, History history) {
    check(mockMaker != null);
    check(history != null);
    return new NiceMockMaker(mockMaker, history);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    T mock = mockMaker.make(type, name);
    history.add(stubbingNice(mock));
    return mock;
  }

  private static Stubbing stubbingNice(Object mock) {
    return stubbing(onInstance(mock), new Handler() {
      public Object handle(Invocation invocation) {
        return defaultValue(invocation.method.getReturnType());
      }
    });
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return "onInstance(" + mock + ")";
      }
    };
  }
}
