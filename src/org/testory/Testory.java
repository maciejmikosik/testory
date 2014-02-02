package org.testory;

import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;
import static org.testory.util.Effect.getReturned;
import static org.testory.util.Effect.getThrown;
import static org.testory.util.Effect.hasReturned;
import static org.testory.util.Effect.hasReturnedObject;
import static org.testory.util.Effect.hasThrown;
import static org.testory.util.Effect.returned;
import static org.testory.util.Effect.returnedVoid;
import static org.testory.util.Effect.thrown;
import static org.testory.util.Matchers.isMatcher;
import static org.testory.util.Matchers.match;
import static org.testory.util.Samples.isSampleable;
import static org.testory.util.Samples.sample;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;
import org.testory.util.Effect;

public class Testory {
  private static History history = new History();

  public static void givenTest(Object test) {
    for (final Field field : test.getClass().getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            field.setAccessible(true);
            return null;
          }
        });
        try {
          Object value = field.get(test);
          boolean isInitialized = field.getType().isPrimitive()
              ? !value.equals(false) && !value.equals((char) 0) && !value.equals((byte) 0)
                  && !value.equals((short) 0) && !value.equals(0) && !value.equals((long) 0)
                  && !value.equals((float) 0) && !value.equals((double) 0)
              : value != null;
          if (!isInitialized) {
            field.set(test, mockOrSample(field.getType(), field.getName()));
          }
        } catch (IllegalAccessException e) {
          throw new Error(e);
        }
      }
    }
  }

  private static Object mockOrSample(Class<?> type, String name) {
    if (isProxiable(type)) {
      return mock(type, namedMockHandler(name));
    } else if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      Object array = Array.newInstance(componentType, 1);
      Array.set(array, 0, mockOrSample(componentType, name + "[0]"));
      return array;
    } else if (isSampleable(type)) {
      return sample(type, name);
    } else {
      throw new TestoryException("cannot mock or sample " + type.getName() + " " + name);
    }
  }

  /**
   * Reserved for future use.
   */
  @Deprecated
  public static Closure given(Closure closure) {
    throw new TestoryException("\n\tgiven(Closure) is confusing, do not use it\n");
  }

  public static <T> T given(T object) {
    return object;
  }

  public static void given(boolean primitive) {}

  public static void given(double primitive) {}

  public static <T> T givenTry(T object) {
    check(object != null);
    check(isProxiable(object.getClass()));
    Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        try {
          return invoke(invocation);
        } catch (Throwable e) {
          return null;
        }
      }
    };
    return proxyWrapping(object, typing, handler);
  }

  public static void givenTimes(int number, Closure closure) {
    check(number >= 0);
    check(closure != null);
    for (int i = 0; i < number; i++) {
      try {
        closure.invoke();
      } catch (Throwable throwable) {
        throw gently(throwable);
      }
    }
  }

  public static <T> T givenTimes(final int number, T object) {
    check(number >= 0);
    check(object != null);
    check(isProxiable(object.getClass()));
    Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        for (int i = 0; i < number; i++) {
          invoke(invocation);
        }
        return null;
      }
    };
    return proxyWrapping(object, typing, handler);
  }

  public static <T> T mock(Class<T> type) {
    check(isProxiable(type));
    return mock(type, unnamedMockHandler());
  }

  private static <T> T mock(Class<T> type, final Handler defaultHandler) {
    check(isProxiable(type));
    Typing typing = type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        history.logInvocation(invocation);
        Handler stubbedHandler = history.getStubbedHandlerFor(invocation);
        return stubbedHandler != null
            ? stubbedHandler.handle(invocation)
            : defaultHandler.handle(invocation);
      }
    };
    return (T) proxy(typing, handler);
  }

  private static Handler unnamedMockHandler() {
    return new Handler() {
      public Object handle(Invocation invocation) {
        return invocation.method.getName().equals("toString")
            ? "mock_" + invocation.instance.getClass().getName() + "_"
                + System.identityHashCode(invocation.instance)
            : invocation.method.getName().equals("equals") && invocation.arguments.size() == 1
                ? invocation.instance == invocation.arguments.get(0)
                : invocation.method.getName().equals("hashCode")
                    && invocation.arguments.size() == 0
                    ? System.identityHashCode(invocation.instance)
                    : null;
      }
    };
  }

  private static Handler namedMockHandler(final String name) {
    return new Handler() {
      public Object handle(Invocation invocation) {
        return invocation.method.getName().equals("toString")
            ? name
            : invocation.method.getName().equals("equals") && invocation.arguments.size() == 1
                ? invocation.instance == invocation.arguments.get(0)
                : invocation.method.getName().equals("hashCode")
                    && invocation.arguments.size() == 0
                    ? name.hashCode()
                    : null;
      }
    };
  }

  public static <T> T given(final Handler will, T mock) {
    check(will != null);
    check(mock != null);
    Typing typing = typing(mock.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        history.logStubbing(will, history.buildOnUsingCaptors(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, typing, handler);
  }

  public static void given(Handler will, On on) {
    check(will != null);
    check(on != null);
    history.logStubbing(will, on);
  }

  public static Handler willReturn(@Nullable final Object object) {
    return new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return object;
      }
    };
  }

  public static Handler willThrow(final Throwable throwable) {
    check(throwable != null);
    return new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  public static <T> T any(Class<T> type) {
    check(type != null);
    check(!type.isPrimitive());
    return history.logCaptor(type);
  }

  public static <T> T when(T object) {
    history.purge();
    history.logWhen(returned(object));
    boolean isProxiable = object != null && isProxiable(object.getClass());
    if (isProxiable) {
      Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
      Handler handler = new Handler() {
        public Object handle(Invocation invocation) {
          history.logWhen(effectOfInvoke(invocation));
          return null;
        }
      };
      return proxyWrapping(object, typing, handler);
    } else {
      return null;
    }
  }

  public static void when(Closure closure) {
    check(closure != null);
    history.purge();
    history.logWhen(effectOfInvoke(closure));
  }

  private static Effect effectOfInvoke(Closure closure) {
    Object object;
    try {
      object = closure.invoke();
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return returned(object);
  }

  private static Effect effectOfInvoke(Invocation invocation) {
    Object object;
    try {
      object = invoke(invocation);
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return invocation.method.getReturnType() == void.class
        ? returnedVoid()
        : returned(object);
  }

  public static void when(boolean value) {
    when((Object) value);
  }

  public static void when(char value) {
    when((Object) value);
  }

  public static void when(byte value) {
    when((Object) value);
  }

  public static void when(short value) {
    when((Object) value);
  }

  public static void when(int value) {
    when((Object) value);
  }

  public static void when(long value) {
    when((Object) value);
  }

  public static void when(float value) {
    when((Object) value);
  }

  public static void when(double value) {
    when((Object) value);
  }

  public static void thenReturned(@Nullable Object objectOrMatcher) {
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasReturnedObject(effect)
        && (areEqualDeep(objectOrMatcher, getReturned(effect)) || objectOrMatcher != null
            && isMatcher(objectOrMatcher) && match(objectOrMatcher, getReturned(effect)));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected returned", objectOrMatcher) //
          + formatBut(effect));
    }
  }

  public static void thenReturned(boolean value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(char value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(byte value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(short value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(int value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(long value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(float value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(double value) {
    thenReturned((Object) value);
  }

  public static void thenReturned() {
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasReturned(effect);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected returned", "") //
          + formatBut(effect));
    }
  }

  public static void thenThrown(Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasThrown(effect) && match(matcher, getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", matcher) //
          + formatBut(effect));
    }
  }

  public static void thenThrown(Throwable throwable) {
    check(throwable != null);
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasThrown(effect) && areEqualDeep(throwable, getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", throwable) //
          + formatBut(effect));
    }
  }

  public static void thenThrown(Class<? extends Throwable> type) {
    check(type != null);
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasThrown(effect) && type.isInstance(getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", type.getName()) //
          + formatBut(effect));
    }
  }

  public static void thenThrown() {
    Effect effect = history.getLastWhenEffect();
    boolean expected = hasThrown(effect);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", "") //
          + formatBut(effect));
    }
  }

  private static String formatBut(Effect effect) {
    return hasReturned(effect)
        ? hasReturnedObject(effect)
            ? formatSection("but returned", getReturned(effect))
            : formatSection("but returned", "void")
        : "" //
            + formatSection("but thrown", getThrown(effect)) //
            + "\n" //
            + printStackTrace(getThrown(effect));
  }

  public static void then(boolean condition) {
    if (!condition) {
      throw assertionError("\n" //
          + formatSection("expected", "true") //
          + formatSection("but was", "false"));
    }
  }

  public static void then(@Nullable Object object, Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    if (!match(matcher, object)) {
      throw assertionError("\n" //
          + formatSection("expected", matcher) //
          + formatSection("but was", object));
    }
  }

  public static void thenEqual(@Nullable Object object, @Nullable Object expected) {
    if (!areEqualDeep(object, expected)) {
      throw assertionError("\n" //
          + formatSection("expected", expected) //
          + formatSection("but was", object));
    }
  }

  public static <T> T thenCalled(T mock) {
    check(mock != null);
    Typing typing = typing(mock.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(final Invocation invocation) throws Throwable {
        On on = history.buildOnUsingCaptors(invocation);
        int number = numberOfCalls(on, history.getInvocations());
        boolean expected = (number == 1);
        if (!expected) {
          throw assertionError("\n" //
              + formatSection("expected called", on));
        }
        return null;
      }
    };
    return proxyWrapping(mock, typing, handler);
  }

  public static void thenCalled(On on) {
    check(on != null);
    int number = numberOfCalls(on, history.getInvocations());
    boolean expected = (number == 1);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected called", on));
    }
  }

  public static <T> T thenCalledTimes(final int number, T mock) {
    check(number >= 0);
    check(mock != null);
    Typing typing = typing(mock.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(final Invocation invocation) throws Throwable {
        On on = history.buildOnUsingCaptors(invocation);
        int numberOfCalls = numberOfCalls(on, history.getInvocations());
        boolean expected = (numberOfCalls == number);
        if (!expected) {
          throw assertionError("\n" //
              + formatSection("expected called times " + number, on));
        }
        return null;
      }
    };
    return proxyWrapping(mock, typing, handler);
  }

  public static void thenCalledTimes(int number, On on) {
    check(number >= 0);
    check(on != null);
    int numberOfCalls = numberOfCalls(on, history.getInvocations());
    boolean expected = (numberOfCalls == number);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected called times " + number, on));
    }
  }

  public static <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(mock != null);
    Typing typing = typing(mock.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(final Invocation invocation) throws Throwable {
        On on = history.buildOnUsingCaptors(invocation);
        int numberOfCalls = numberOfCalls(on, history.getInvocations());
        boolean expected = (match(numberMatcher, numberOfCalls));
        if (!expected) {
          throw assertionError("\n" //
              + formatSection("expected called times " + numberMatcher, on));
        }
        return null;
      }
    };
    return proxyWrapping(mock, typing, handler);
  }

  public static void thenCalledTimes(Object numberMatcher, On on) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(on != null);
    int numberOfCalls = numberOfCalls(on, history.getInvocations());
    boolean expected = (match(numberMatcher, numberOfCalls));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected called times " + numberMatcher, on));
    }
  }

  private static int numberOfCalls(On on, List<Invocation> invocations) {
    int counter = 0;
    for (Invocation invocation : invocations) {
      if (on.matches(invocation)) {
        counter++;
      }
    }
    return counter;
  }

  private static String formatSection(String caption, @Nullable Object content) {
    return "" //
        + "  " + caption + "\n" //
        + "    " + print(content) + "\n";
  }

  private static void check(boolean condition) {
    if (!condition) {
      throw new TestoryException();
    }
  }

  private static <T> T proxyWrapping(final T wrapped, Typing typing, final Handler handler) {
    return (T) proxy(typing, new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return handler.handle(invocation(invocation.method, wrapped, invocation.arguments));
      }
    });
  }

  private static TestoryAssertionError assertionError(String message) {
    TestoryAssertionError error = new TestoryAssertionError(message);
    cloakStackTrace(error);
    return error;
  }

  private static void cloakStackTrace(Throwable throwable) {
    StackTraceElement[] stackTrace = throwable.getStackTrace();

    int index = -1;
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      if (stackTrace[i].getClassName().equals(Testory.class.getName())) {
        index = i;
        break;
      }
    }
    if (index == -1 || index == stackTrace.length - 1) {
      throw new Error();
    }
    throwable.setStackTrace(new StackTraceElement[] { stackTrace[index + 1] });
  }
}
