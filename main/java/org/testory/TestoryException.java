package org.testory;

import static org.testory.common.Throwables.newLinkageError;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testory.proxy.Proxies;

public class TestoryException extends RuntimeException {
  public TestoryException() {}

  public TestoryException(String message) {
    super(message);
  }

  public TestoryException(Throwable cause) {
    super(cause);
  }

  public TestoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public static void check(boolean condition) {
    check(condition, "illegal testory usage");
  }

  public static void check(boolean condition, String message) {
    if (!condition) {
      throw decorate(message, new TestoryException());
    }
  }

  private static TestoryException decorate(String message, TestoryException exception) {
    StackTraceElement[] trace = exception.getStackTrace();
    StackTraceElement[] digestTrace = array(findPrecondition(trace), findCaller(trace));
    String decoratedMessage = "" //
        + "\n" //
        + "  " + message + "\n" //
        + "  failed precondition\n" //
        + "    " + trimCondition(sourceLine(findPrecondition(trace))) + "\n";
    TestoryException decorated = new TestoryException(decoratedMessage, exception);
    decorated.setStackTrace(digestTrace);
    return decorated;
  }

  private static String trimCondition(String line) {
    return line.trim().replace("check(", "").replace(");", "");
  }

  private static StackTraceElement findCaller(StackTraceElement[] stackTrace) {
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      String name = stackTrace[i].getClassName();
      if (name.equals(Testory.class.getName()) || name.startsWith(Proxies.class.getName())) {
        for (int j = i + 1; j < stackTrace.length; j++) {
          if (stackTrace[j].getLineNumber() >= 0) {
            return stackTrace[j];
          }
        }
        throw new Error();
      }
    }
    throw new Error();
  }

  private static StackTraceElement findPrecondition(StackTraceElement[] stackTrace) {
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      if (stackTrace[i].getClassName().equals(TestoryException.class.getName())
          && stackTrace[i].getMethodName().equals("check")) {
        return stackTrace[i + 1];
      }
    }
    throw new Error();
  }

  private static String sourceLine(StackTraceElement trace) {
    int lineNumber = trace.getLineNumber();
    BufferedReader reader = null;
    try {
      Class<?> type = Class.forName(trace.getClassName());
      while (type.getEnclosingClass() != null) {
        type = type.getEnclosingClass();
      }
      String simpleName = type.getSimpleName();
      InputStream resourceAsStream = type.getResourceAsStream(simpleName + ".java");
      if (resourceAsStream == null) {
        throw new LinkageError(simpleName + ".java not found");
      }
      reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      for (int i = 1; i < lineNumber; i++) {
        reader.readLine();
      }
      return reader.readLine().trim();
    } catch (ReflectiveOperationException e) {
      throw newLinkageError(e);
    } catch (IOException e) {
      throw newLinkageError(e);
    } finally {
      closeQuietly(reader);
    }
  }

  private static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException e) {}
  }

  private static <T> T[] array(T... values) {
    return values;
  }
}
