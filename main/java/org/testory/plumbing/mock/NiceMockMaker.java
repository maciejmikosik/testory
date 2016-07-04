package org.testory.plumbing.mock;

import static org.testory.TestoryException.check;
import static org.testory.common.Classes.defaultValue;
import static org.testory.plumbing.Stubbing.stubbing;

import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class NiceMockMaker {
  public static Maker nice(final Maker mockMaker, final History history) {
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        check(type != null);
        check(name != null);
        T mock = mockMaker.make(type, name);
        history.add(stubNice(mock));
        return mock;
      }
    };
  }

  private static Stubbing stubNice(Object mock) {
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
