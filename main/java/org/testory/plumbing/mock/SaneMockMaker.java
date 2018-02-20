package org.testory.plumbing.mock;

import static java.util.Objects.deepEquals;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.mock.Stubbed.stubbed;
import static org.testory.proxy.handler.ReturningHandler.returning;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.testory.plumbing.Maker;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class SaneMockMaker implements Maker {
  private final History history;
  private final Maker mockMaker;

  private SaneMockMaker(History history, Maker mockMaker) {
    this.history = history;
    this.mockMaker = mockMaker;
  }

  public static Maker sane(History history, Maker mockMaker) {
    check(history != null);
    check(mockMaker != null);
    return new SaneMockMaker(history, mockMaker);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    T mock = mockMaker.make(type, name);
    history.add(stubbedEquals(mock, name));
    history.add(stubbedHashCode(mock, name));
    history.add(stubbedToString(mock, name));
    if (Throwable.class.isAssignableFrom(type)) {
      history.add(stubbedFillInStackTrace(mock));
      history.add(stubbedPrintStackTrace(mock));
      history.add(stubbedPrintStackTracePrintStream(mock));
      history.add(stubbedPrintStackTracePrintWriter(mock));
    }
    return mock;
  }

  private static Stubbed stubbedEquals(final Object mock, String name) {
    return stubbed(onInvocation(mock, "equals", Object.class), new Handler() {
      public Object handle(Invocation invocation) {
        return mock == invocation.arguments.get(0);
      }
    });
  }

  private static Stubbed stubbedHashCode(Object mock, String name) {
    return stubbed(onInvocation(mock, "hashCode"), returning(name.hashCode()));
  }

  private static Stubbed stubbedToString(Object mock, String name) {
    return stubbed(onInvocation(mock, "toString"), returning(name));
  }

  private static Stubbed stubbedFillInStackTrace(Object mock) {
    return stubbed(onInvocation(mock, "fillInStackTrace"), returning(mock));
  }

  private static Stubbed stubbedPrintStackTrace(Object mock) {
    return stubbed(onInvocation(mock, "printStackTrace"), new Handler() {
      public Object handle(Invocation invocation) throws IOException {
        System.err.write(invocation.instance.toString().getBytes());
        return null;
      }
    });
  }

  private static Stubbed stubbedPrintStackTracePrintStream(Object mock) {
    return stubbed(onInvocation(mock, "printStackTrace", PrintStream.class), new Handler() {
      public Object handle(Invocation invocation) throws IOException {
        PrintStream printStream = (PrintStream) invocation.arguments.get(0);
        printStream.write(invocation.instance.toString().getBytes());
        return null;
      }
    });
  }

  private static Stubbed stubbedPrintStackTracePrintWriter(Object mock) {
    return stubbed(onInvocation(mock, "printStackTrace", PrintWriter.class), new Handler() {
      public Object handle(Invocation invocation) {
        PrintWriter printWriter = (PrintWriter) invocation.arguments.get(0);
        printWriter.write(invocation.instance.toString());
        return null;
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
