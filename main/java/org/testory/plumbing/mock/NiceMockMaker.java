package org.testory.plumbing.mock;

import static java.lang.String.format;
import static org.testory.common.Classes.defaultValue;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.mock.Stubbed.stubbed;

import org.testory.plumbing.Maker;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class NiceMockMaker implements Maker {
  private final History history;
  private final Maker mockMaker;

  private NiceMockMaker(History history, Maker mockMaker) {
    this.history = history;
    this.mockMaker = mockMaker;
  }

  public static Maker nice(History history, Maker mockMaker) {
    check(history != null);
    check(mockMaker != null);
    return new NiceMockMaker(history, mockMaker);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    T mock = mockMaker.make(type, name);
    history.add(stubbedNice(mock));
    return mock;
  }

  private static Stubbed stubbedNice(Object mock) {
    return stubbed(onInstance(mock), new Handler() {
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
        return format("onInstance(%s)", mock);
      }
    };
  }
}
