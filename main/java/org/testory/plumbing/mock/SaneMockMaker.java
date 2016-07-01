package org.testory.plumbing.mock;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryException.check;
import static org.testory.plumbing.History.add;
import static org.testory.plumbing.Stubbing.stubbing;

import org.testory.plumbing.History;
import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class SaneMockMaker {
  public static Maker sane(final Maker mockMaker, final ThreadLocal<History> mutableHistory) {
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        check(type != null);
        check(name != null);
        T mock = mockMaker.make(type, name);
        mutableHistory.set(addAll(mutableHistory.get(),
            stubbingEquals(mock, name),
            stubbingHashCode(mock, name),
            stubbingToString(mock, name)));
        return mock;
      }
    };
  }

  // TODO implement non-static History.add
  private static History addAll(History history, Object... events) {
    History newHistory = history;
    for (Object event : events) {
      newHistory = add(event, newHistory);
    }
    return newHistory;
  }

  private static Stubbing stubbingEquals(final Object mock, String name) {
    return stubbing(onInvocation(mock, "equals", Object.class), new Handler() {
      public Object handle(Invocation invocation) {
        return mock == invocation.arguments.get(0);
      }
    });
  }

  private static Stubbing stubbingHashCode(final Object mock, final String name) {
    return stubbing(onInvocation(mock, "hashCode"), new Handler() {
      public Object handle(Invocation invocation) {
        return name.hashCode();
      }
    });
  }

  private static Stubbing stubbingToString(final Object mock, final String name) {
    return stubbing(onInvocation(mock, "toString"), new Handler() {
      public Object handle(Invocation invocation) {
        return name;
      }
    });
  }

  private static InvocationMatcher onInvocation(
      final Object instance, final String methodName, final Class<?>... parameters) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == instance
            && deepEquals(invocation.method.getName(), methodName)
            && deepEquals(invocation.method.getParameterTypes(), parameters);
      }
    };
  }
}
