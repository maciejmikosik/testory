package org.testory;

import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.TestoryException.check;
import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Classes.hasMethod;
import static org.testory.common.Classes.isFinal;
import static org.testory.common.Classes.isStatic;
import static org.testory.common.Classes.setAccessible;
import static org.testory.common.Objects.areEqual;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.plumbing.Calling.calling;
import static org.testory.plumbing.Calling.callings;
import static org.testory.plumbing.Capturing.capturedAnys;
import static org.testory.plumbing.Capturing.consumeAnys;
import static org.testory.plumbing.Capturing.CapturingAny.capturingAny;
import static org.testory.plumbing.History.add;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.Inspecting.findLastInspecting;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.Mocking.mocking;
import static org.testory.plumbing.Mocking.nameMock;
import static org.testory.plumbing.Purging.mark;
import static org.testory.plumbing.Purging.purge;
import static org.testory.plumbing.Stubbing.findStubbing;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.proxy.Invocation.invocation;
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
import static org.testory.util.Invocations.invoke;
import static org.testory.util.Matchers.asMatcher;
import static org.testory.util.Matchers.isMatcher;
import static org.testory.util.Samples.isSampleable;
import static org.testory.util.Samples.sample;
import static org.testory.util.any.Anyvocation.anyvocation;
import static org.testory.util.any.Matcherizes.matcherize;
import static org.testory.util.any.Repairs.repair;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.plumbing.Calling;
import org.testory.plumbing.History;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.Mocking;
import org.testory.plumbing.Stubbing;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;
import org.testory.util.Effect;
import org.testory.util.any.Any;
import org.testory.util.any.Anyvocation;

public class Testory {
  private static ThreadLocal<History> localHistory = new ThreadLocal<History>() {
    protected History initialValue() {
      return history(new ArrayList<Object>());
    }
  };

  private static History getHistory() {
    return localHistory.get();
  }

  private static void setHistory(History history) {
    localHistory.set(history);
  }

  public static void givenTest(Object test) {
    setHistory(purge(mark(getHistory())));
    try {
      for (final Field field : test.getClass().getDeclaredFields()) {
        if (!isStatic(field) && !isFinal(field)) {
          setAccessible(field);
          if (areEqual(defaultValue(field.getType()), field.get(test))) {
            check(canMockOrSample(field.getType()), "cannot inject field: " + field.getName());
            field.set(test, mockOrSample(field.getType(), field.getName()));
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new Error(e);
    }
  }

  private static boolean canMockOrSample(Class<?> type) {
    return isProxiable(type) || type.isArray() || isSampleable(type);
  }

  private static Object mockOrSample(Class<?> type, String name) {
    if (isProxiable(type)) {
      Object mock = rawMock(type);
      log(mocking(mock, name));
      stubNice(mock);
      stubObject(mock, name);
      return mock;
    } else if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      Object array = Array.newInstance(componentType, 1);
      Array.set(array, 0, mockOrSample(componentType, name + "[0]"));
      return array;
    } else if (isSampleable(type)) {
      return sample(type, name);
    } else {
      throw new IllegalArgumentException("cannot mock or sample " + type.getName() + " " + name);
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
    check(type != null);
    check(isProxiable(type));
    final T mock = rawMock(type);
    String name = nameMock(mock, getHistory());
    log(mocking(mock, name));
    stubNice(mock);
    stubObject(mock, name);
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
      public Object handle(Invocation invocation) throws Throwable {
        check(isMock(invocation.instance));
        check(isStubbed(invocation));
        log(calling(invocation));
        Stubbing stubbing = findStubbing(invocation, getHistory()).get();
        return stubbing.handler.handle(invocation);
      }
    };
    return (T) proxy(typing, compatible(handler));
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
      public Object handle(Invocation invocation) {
        return defaultValue(invocation.method.getReturnType());
      }
    }, onInstance(mock));
  }

  private static void stubObject(final Object mock, final String name) {
    final Object implementation = new Object() {
      public String toString() {
        return name;
      }

      public boolean equals(Object obj) {
        return mock == obj;
      }

      public int hashCode() {
        return name.hashCode();
      }
    };
    given(willTarget(implementation), new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return mock == invocation.instance
            && hasMethod(invocation.method.getName(), invocation.method.getParameterTypes(),
                implementation.getClass());
      }
    });
  }

  public static <T> T given(final Handler will, T mock) {
    check(will != null);
    check(mock != null);
    check(isMock(mock));
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        log(stubbing(capture(invocation), will));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public static void given(Handler will, InvocationMatcher invocationMatcher) {
    check(will != null);
    check(invocationMatcher != null);
    log(stubbing(invocationMatcher, will));
  }

  public static Handler willReturn(@Nullable final Object object) {
    return new Handler() {
      public Object handle(Invocation invocation) {
        return object;
      }
    };
  }

  public static Handler willThrow(final Throwable throwable) {
    check(throwable != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable.fillInStackTrace();
      }
    };
  }

  public static Handler willRethrow(final Throwable throwable) {
    check(throwable != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  public static Handler willSpy(final Object real) {
    check(real != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return invoke(invocation(invocation.method, real, invocation.arguments));
      }
    };
  }

  private static Handler willTarget(final Object target) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        String methodName = invocation.method.getName();
        Class<?>[] parameters = invocation.method.getParameterTypes();
        Method method = target.getClass().getDeclaredMethod(methodName, parameters);
        return invoke(invocation(method, target, invocation.arguments));
      }
    };
  }

  public static <T> T any(Class<T> type) {
    check(type != null);
    return anyImpl(Any.any(type));
  }

  public static <T> T any(Class<T> type, Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    return anyImpl(Any.any(type, asMatcher(matcher)));
  }

  public static boolean a(boolean value) {
    return anyImpl(Any.a(value));
  }

  public static char a(char value) {
    return anyImpl(Any.a(value));
  }

  public static byte a(byte value) {
    return anyImpl(Any.a(value));
  }

  public static short a(short value) {
    return anyImpl(Any.a(value));
  }

  public static int a(int value) {
    return anyImpl(Any.a(value));
  }

  public static long a(long value) {
    return anyImpl(Any.a(value));
  }

  public static float a(float value) {
    return anyImpl(Any.a(value));
  }

  public static double a(double value) {
    return anyImpl(Any.a(value));
  }

  public static <T> T a(T value) {
    check(value != null);
    return anyImpl(Any.a(value));
  }

  public static void the(boolean value) {
    check(false);
  }

  public static void the(double value) {
    check(false);
  }

  public static <T> T the(T instance) {
    check(instance != null);
    return anyImpl(Any.the(instance));
  }

  private static <T> T anyImpl(Any any) {
    log(capturingAny(any));
    return (T) any.token;
  }

  public static InvocationMatcher onInstance(final Object mock) {
    check(mock != null);
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return "onInstance(" + mock + ")";
      }
    };
  }

  public static InvocationMatcher onReturn(final Class<?> type) {
    check(type != null);
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return type == invocation.method.getReturnType();
      }

      public String toString() {
        return "onReturn(" + type.getName() + ")";
      }
    };
  }

  public static <T> T when(T object) {
    setHistory(mark(purge(getHistory())));
    log(inspecting(returned(object)));
    boolean isProxiable = object != null && isProxiable(object.getClass());
    if (isProxiable) {
      Handler handler = new Handler() {
        public Object handle(Invocation invocation) {
          log(inspecting(effectOfInvoke(invocation)));
          setHistory(mark(getHistory()));
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
    log(inspecting(effectOfInvoke(closure)));
    setHistory(mark(purge(getHistory())));
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
    Effect effect = getLastEffect();
    boolean expected = hasReturnedObject(effect)
        && (areEqualDeep(objectOrMatcher, getReturned(effect)) || objectOrMatcher != null
            && isMatcher(objectOrMatcher)
            && asMatcher(objectOrMatcher).matches(getReturned(effect)));
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
    Effect effect = getLastEffect();
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
    Effect effect = getLastEffect();
    boolean expected = hasThrown(effect) && asMatcher(matcher).matches(getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", matcher) //
          + formatBut(effect));
    }
  }

  public static void thenThrown(Throwable throwable) {
    check(throwable != null);
    Effect effect = getLastEffect();
    boolean expected = hasThrown(effect) && areEqualDeep(throwable, getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", throwable) //
          + formatBut(effect));
    }
  }

  public static void thenThrown(Class<? extends Throwable> type) {
    check(type != null);
    Effect effect = getLastEffect();
    boolean expected = hasThrown(effect) && type.isInstance(getThrown(effect));
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", type.getName()) //
          + formatBut(effect));
    }
  }

  public static void thenThrown() {
    Effect effect = getLastEffect();
    boolean expected = hasThrown(effect);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected thrown", "") //
          + formatBut(effect));
    }
  }

  private static Effect getLastEffect() {
    Optional<Inspecting> inspecting = findLastInspecting(getHistory());
    check(inspecting.isPresent());
    return inspecting.get().effect;
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
    if (!asMatcher(matcher).matches(object)) {
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
    check(isMock(mock));
    return thenCalledTimes(exactly(1), mock);
  }

  public static void thenCalled(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
    thenCalledTimes(exactly(1), invocationMatcher);
  }

  public static <T> T thenCalledTimes(int number, T mock) {
    check(number >= 0);
    check(mock != null);
    check(isMock(mock));
    return thenCalledTimes(exactly(number), mock);
  }

  public static void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    check(number >= 0);
    check(invocationMatcher != null);
    thenCalledTimes(exactly(number), invocationMatcher);
  }

  public static <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(mock != null);
    check(isMock(mock));
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledTimes(numberMatcher, capture(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public static void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(invocationMatcher != null);
    int numberOfCalls = 0;
    for (Calling calling : callings(getHistory())) {
      if (invocationMatcher.matches(calling.invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = asMatcher(numberMatcher).matches(numberOfCalls);
    if (!expected) {
      throw assertionError("\n" //
          + formatSection("expected called times " + numberMatcher, invocationMatcher)
          + formatSection("but called", "times " + numberOfCalls));
    }
  }

  private static Matcher exactly(final int number) {
    return new Matcher() {
      public boolean matches(Object item) {
        return item.equals(number);
      }

      public String toString() {
        return "" + number;
      }
    };
  }

  public static void log(Object event) {
    setHistory(add(event, getHistory()));
  }

  private static <T> boolean isMock(T mock) {
    return Mocking.isMock(mock, getHistory());
  }

  private static boolean isStubbed(Invocation invocation) {
    return findStubbing(invocation, getHistory()).isPresent();
  }

  private static String formatSection(String caption, @Nullable Object content) {
    return "" //
        + "  " + caption + "\n" //
        + "    " + print(content) + "\n";
  }

  private static <T> T proxyWrapping(final T wrapped, final Handler handler) {
    Typing typing = typing(wrapped.getClass(), new HashSet<Class<?>>());
    return (T) proxy(typing, new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        handler.handle(invocation(invocation.method, wrapped, invocation.arguments));
        return defaultValue(invocation.method.getReturnType());
      }
    });
  }

  private static InvocationMatcher capture(Invocation invocation) {
    List<Any> anys = capturedAnys(getHistory());
    setHistory(consumeAnys(getHistory()));
    Anyvocation anyvocation = anyvocation(invocation.method, invocation.instance,
        invocation.arguments, anys);
    check(canRepair(anyvocation));
    return convert(matcherize(repair(anyvocation).get()));
  }

  private static boolean canRepair(Anyvocation anyvocation) {
    return repair(anyvocation).isPresent();
  }

  private static InvocationMatcher convert(final Matcher invocationMatcher) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocationMatcher.matches(invocation);
      }

      public String toString() {
        return invocationMatcher.toString();
      }
    };
  }
}
