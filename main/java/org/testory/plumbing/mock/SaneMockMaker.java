package org.testory.plumbing.mock;

import static java.util.Objects.deepEquals;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.proxy.handler.ReturningHandler.returning;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
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
    history.add(stubbingEquals(mock, name));
    history.add(stubbingHashCode(mock, name));
    history.add(stubbingToString(mock, name));
    if (Throwable.class.isAssignableFrom(type)) {
      history.add(stubbingFillInStackTrace(mock));
      history.add(stubbingPrintStackTrace(mock));
      history.add(stubbingPrintStackTracePrintStream(mock));
      history.add(stubbingPrintStackTracePrintWriter(mock));
    }
    return mock;
  }

  private static Stubbing stubbingEquals(final Object mock, String name) {
    return stubbing(onInvocation(mock, "equals", Object.class), new Handler() {
      public Object handle(Invocation invocation) {
        return mock == invocation.arguments.get(0);
      }
    });
  }

  private static Stubbing stubbingHashCode(Object mock, String name) {
    return stubbing(onInvocation(mock, "hashCode"), returning(name.hashCode()));
  }

  private static Stubbing stubbingToString(Object mock, String name) {
    return stubbing(onInvocation(mock, "toString"), returning(name));
  }

  private static Stubbing stubbingFillInStackTrace(Object mock) {
    return stubbing(onInvocation(mock, "fillInStackTrace"), returning(mock));
  }

  private static Stubbing stubbingPrintStackTrace(Object mock) {
    return stubbing(onInvocation(mock, "printStackTrace"), new Handler() {
      public Object handle(Invocation invocation) throws IOException {
        System.err.write(invocation.instance.toString().getBytes());
        return null;
      }
    });
  }

  private static Stubbing stubbingPrintStackTracePrintStream(Object mock) {
    return stubbing(onInvocation(mock, "printStackTrace", PrintStream.class), new Handler() {
      public Object handle(Invocation invocation) throws IOException {
        PrintStream printStream = (PrintStream) invocation.arguments.get(0);
        printStream.write(invocation.instance.toString().getBytes());
        return null;
      }
    });
  }

  private static Stubbing stubbingPrintStackTracePrintWriter(Object mock) {
    return stubbing(onInvocation(mock, "printStackTrace", PrintWriter.class), new Handler() {
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
