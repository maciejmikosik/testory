package org.testory;

import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;
import static org.testory.common.Classes.zeroOrNull;
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxies;
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
      Object mock = rawMock(type);
      stubNice(mock);
      stubObject(mock, name, name.hashCode());
      return mock;
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
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        try {
          return invoke(invocation);
        } catch (Throwable e) {
          return null;
        }
      }
    };
    return proxyWrapping(object, handler);
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
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        for (int i = 0; i < number; i++) {
          invoke(invocation);
        }
        return null;
      }
    };
    return proxyWrapping(object, handler);
  }

  public static <T> T mock(Class<T> type) {
    check(isProxiable(type));
    final T mock = rawMock(type);
    int hash = System.identityHashCode(mock);
    stubNice(mock);
    stubObject(mock, "mock_" + hash + "_" + type.getName(), hash);
    return mock;
  }

  public static <T> T spy(T real) {
    check(real != null);
    Class<T> type = (Class<T>) real.getClass();
    T mock = mock(type);
    given(willSpy(real), onInstance(mock));
    return mock;
  }

  private static <T> T rawMock(Class<T> type) {
    check(isProxiable(type));
    Typing typing = type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        history.logInvocation(invocation);
        if (history.hasStubbedHandlerFor(invocation)) {
          return history.getStubbedHandlerFor(invocation).handle(invocation);
        } else {
          throw new TestoryException("purged mock");
        }
      }
    };
    T mock = (T) proxy(typing, compatible(handler));
    history.logMocking(mock);
    return mock;
  }

  private static Handler compatible(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Object returned;
        try {
          returned = handler.handle(invocation);
        } catch (Throwable throwable) {
          check(canThrow(throwable, invocation.method));
          throw throwable;
        }
        check(canReturn(returned, invocation.method) || canReturnVoid(returned, invocation.method));
        return returned;
      }

      private boolean canReturnVoid(Object returned, Method method) {
        return method.getReturnType() == void.class && returned == null;
      }
    };
  }

  private static void stubNice(Object mock) {
    given(new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Class<?> returnType = invocation.method.getReturnType();
        return returnType.isPrimitive()
            ? zeroOrNull(returnType)
            : null;
      }
    }, onInstance(mock));
  }

  private static void stubObject(final Object mock, String name, int hash) {
    given(willReturn(name), new Captor() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("toString")
            && invocation.method.getParameterTypes().length == 0;
      }
    });
    given(new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return invocation.instance == mock && invocation.arguments.get(0) == mock;
      }
    }, new Captor() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock
            && invocation.method.getName().equals("equals")
            && Arrays.equals(invocation.method.getParameterTypes(), new Class<?>[] { Object.class });
      }
    });
    given(willReturn(hash), new Captor() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("hashCode")
            && invocation.method.getParameterTypes().length == 0;
      }
    });
  }

  public static <T> T given(final Handler will, T mock) {
    check(will != null);
    check(mock != null);
    check(history.isMock(mock));
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        history.logStubbing(will, history.buildCaptorUsingAnys(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public static void given(Handler will, Captor captor) {
    check(will != null);
    check(captor != null);
    history.logStubbing(will, captor);
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
        throw throwable.fillInStackTrace();
      }
    };
  }

  public static Handler willRethrow(final Throwable throwable) {
    check(throwable != null);
    return new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  public static Handler willSpy(final Object real) {
    check(real != null);
    return new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return invoke(invocation(invocation.method, real, invocation.arguments));
      }
    };
  }

  public static <T> T any(Class<T> type) {
    check(type != null);
    check(!type.isPrimitive());
    return history.logAny(type);
  }

  public static <T> T any(Class<T> type, Object matcher) {
    check(type != null);
    check(matcher != null);
    check(isMatcher(matcher));
    check(!type.isPrimitive());
    return history.logAny(type, matcher);
  }

  public static Captor onInstance(final Object mock) {
    check(mock != null);
    return new Captor() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return "onInstance(" + dangerouslyInvokeToStringOnMock(mock) + ")";
      }
    };
  }

  public static Captor onReturn(final Class<?> type) {
    check(type != null);
    return new Captor() {
      public boolean matches(Invocation invocation) {
        return type == invocation.method.getReturnType();
      }

      public String toString() {
        return "onReturn(" + type.getName() + ")";
      }
    };
  }

  private static String dangerouslyInvokeToStringOnMock(Object mock) {
    return mock.toString();
  }

  public static <T> T when(T object) {
    history.purge();
    history.logWhen(returned(object));
    boolean isProxiable = object != null && isProxiable(object.getClass());
    if (isProxiable) {
      Handler handler = new Handler() {
        public Object handle(Invocation invocation) {
          history.logWhen(effectOfInvoke(invocation));
          return null;
        }
      };
      return proxyWrapping(object, handler);
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
    check(history.isMock(mock));
    return thenCalledTimes(exactly(1), mock);
  }

  public static void thenCalled(Captor captor) {
    check(captor != null);
    thenCalledTimes(exactly(1), captor);
  }

  public static <T> T thenCalledTimes(int number, T mock) {
    check(number >= 0);
    check(mock != null);
    check(history.isMock(mock));
    return thenCalledTimes(exactly(number), mock);
  }

  public static void thenCalledTimes(int number, Captor captor) {
    check(number >= 0);
    check(captor != null);
    thenCalledTimes(exactly(number), captor);
  }

  public static <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(mock != null);
    check(history.isMock(mock));
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        thenCalledTimes(numberMatcher, history.buildCaptorUsingAnys(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public static void thenCalledTimes(Object numberMatcher, Captor captor) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(captor != null);
    int numberOfCalls = 0;
    for (Invocation invocation : history.getInvocations()) {
      if (captor.matches(invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = match(numberMatcher, numberOfCalls);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected called times " + numberMatcher, captor));
    }
  }

  private static Object exactly(final int number) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item.equals(number);
      }

      public String toString() {
        return "" + number;
      }
    };
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

  private static <T> T proxyWrapping(final T wrapped, final Handler handler) {
    Typing typing = typing(wrapped.getClass(), new HashSet<Class<?>>());
    return (T) proxy(typing, new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        handler.handle(invocation(invocation.method, wrapped, invocation.arguments));
        Class<?> returnType = invocation.method.getReturnType();
        return returnType.isPrimitive()
            ? zeroOrNull(returnType)
            : null;
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
      String name = stackTrace[i].getClassName();
      if (name.equals(Testory.class.getName()) || name.startsWith(Proxies.class.getName())) {
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
