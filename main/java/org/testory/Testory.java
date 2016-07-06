package org.testory;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.TestoryException.check;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.plumbing.Calling.callings;
import static org.testory.plumbing.Inspecting.findLastInspecting;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.VerifyingInOrder.verifyInOrder;
import static org.testory.plumbing.capture.Anyvocation.anyvocation;
import static org.testory.plumbing.capture.Anyvocation.matcherize;
import static org.testory.plumbing.capture.Anyvocation.repair;
import static org.testory.plumbing.capture.Capturing.capturedAnys;
import static org.testory.plumbing.capture.Capturing.consumeAnys;
import static org.testory.plumbing.capture.Capturing.CapturingAny.capturingAny;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Typing.typing;

import java.util.HashSet;
import java.util.List;

import org.testory.common.Chain;
import org.testory.common.Closure;
import org.testory.common.DiagnosticMatcher;
import org.testory.common.Effect;
import org.testory.common.Effect.Returned;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.Thrown;
import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.common.VoidClosure;
import org.testory.plumbing.Calling;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.capture.Any;
import org.testory.plumbing.capture.Anyvocation;
import org.testory.plumbing.inject.Injector;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class Testory {
  private static final ThreadLocal<Facade> localFacade = new ThreadLocal<Facade>() {
    protected Facade initialValue() {
      return new Facade();
    }
  };

  private static Facade getFacade() {
    return localFacade.get();
  }

  private static Chain<Object> getHistory() {
    return getFacade().history.get();
  }

  private static void setHistory(Chain<Object> history) {
    getFacade().history.set(history);
  }

  public static void givenTest(Object test) {
    Injector injector = getFacade().injector;
    try {
      injector.inject(test);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

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
    Facade facade = getFacade();
    String name = facade.mockNamer.name(type);
    return facade.mockMaker.make(type, name);
  }

  public static <T> T spy(T real) {
    check(real != null);
    Class<T> type = (Class<T>) real.getClass();
    T mock = mock(type);
    given(willSpy(real), onInstance(mock));
    return mock;
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
    check(isMock(mock));
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

  public static InvocationMatcher onRequest(final Class<?> type, final Object... arguments) {
    check(type != null);
    check(arguments != null);
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return type == invocation.method.getReturnType()
            && deepEquals(arguments, invocation.arguments.toArray());
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("onRequest(").append(type.getName());
        for (Object argument : arguments) {
          builder.append(", ").append(argument);
        }
        builder.append(")");
        return builder.toString();
      }
    };
  }

  public static <T> T when(T object) {
    log(inspecting(returned(object)));
    try {
      return proxyWrapping(object, new Handler() {
        public Object handle(Invocation invocation) {
          log(inspecting(effectOfInvoke(invocation)));
          return null;
        }
      });
    } catch (RuntimeException e) {
      return null;
    }
  }

  public static void when(Closure closure) {
    check(closure != null);
    log(inspecting(effectOfInvoke(closure)));
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

  public static void when(VoidClosure closure) {
    check(closure != null);
    log(inspecting(effectOfInvoke(closure)));
  }

  private static Effect effectOfInvoke(VoidClosure closure) {
    try {
      closure.invoke();
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return returnedVoid();
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
    boolean expected = effect instanceof ReturnedObject
        && (deepEquals(objectOrMatcher, ((ReturnedObject) effect).object) || objectOrMatcher != null
            && isMatcher(objectOrMatcher)
            && asMatcher(objectOrMatcher).matches(((ReturnedObject) effect).object));
    if (!expected) {
      String diagnosis = objectOrMatcher != null && isMatcher(objectOrMatcher)
          && effect instanceof ReturnedObject
              ? tryFormatDiagnosis(objectOrMatcher, ((ReturnedObject) effect).object)
              : "";
      throw assertionError("\n"
          + formatSection("expected returned", objectOrMatcher)
          + formatBut(effect)
          + diagnosis);
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
    boolean expected = effect instanceof Returned;
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected returned", "")
          + formatBut(effect));
    }
  }

  public static void thenThrown(Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown
        && asMatcher(matcher).matches(((Thrown) effect).throwable);
    if (!expected) {
      String diagnosis = effect instanceof Thrown
          ? tryFormatDiagnosis(matcher, ((Thrown) effect).throwable)
          : "";
      throw assertionError("\n"
          + formatSection("expected thrown", matcher)
          + formatBut(effect)
          + diagnosis);
    }
  }

  public static void thenThrown(Throwable throwable) {
    check(throwable != null);
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown
        && deepEquals(throwable, ((Thrown) effect).throwable);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", throwable)
          + formatBut(effect));
    }
  }

  public static void thenThrown(Class<? extends Throwable> type) {
    check(type != null);
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown && type.isInstance(((Thrown) effect).throwable);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", type.getName())
          + formatBut(effect));
    }
  }

  public static void thenThrown() {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown;
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", "")
          + formatBut(effect));
    }
  }

  private static Effect getLastEffect() {
    Optional<Inspecting> inspecting = findLastInspecting(getHistory());
    check(inspecting.isPresent());
    return inspecting.get().effect;
  }

  private static String formatBut(Effect effect) {
    return effect instanceof Returned
        ? effect instanceof ReturnedObject
            ? formatSection("but returned", ((ReturnedObject) effect).object)
            : formatSection("but returned", "void")
        : ""
            + formatSection("but thrown", ((Thrown) effect).throwable)
            + "\n"
            + printStackTrace(((Thrown) effect).throwable);
  }

  public static void then(boolean condition) {
    if (!condition) {
      throw assertionError("\n"
          + formatSection("expected", "true")
          + formatSection("but was", "false"));
    }
  }

  public static void then(@Nullable Object object, Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    if (!asMatcher(matcher).matches(object)) {
      throw assertionError("\n"
          + formatSection("expected", matcher)
          + formatSection("but was", object)
          + tryFormatDiagnosis(matcher, object));
    }
  }

  public static void thenEqual(@Nullable Object object, @Nullable Object expected) {
    if (!deepEquals(object, expected)) {
      throw assertionError("\n"
          + formatSection("expected", expected)
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

  public static <T> T thenCalledNever(T mock) {
    check(mock != null);
    check(isMock(mock));
    return thenCalledTimes(exactly(0), mock);
  }

  public static void thenCalledNever(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
    thenCalledTimes(exactly(0), invocationMatcher);
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
    Chain<Object> history = getHistory();
    for (Calling calling : callings(history)) {
      if (invocationMatcher.matches(calling.invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = asMatcher(numberMatcher).matches(numberOfCalls);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected called times " + numberMatcher, invocationMatcher)
          + formatSection("but called", "times " + numberOfCalls)
          + formatCallings());
    }
  }

  public static <T> T thenCalledInOrder(T mock) {
    check(mock != null);
    check(isMock(mock));
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledInOrder(capture(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public static void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
    Chain<Object> history = getHistory();
    Optional<Chain<Object>> verified = verifyInOrder(invocationMatcher, history);
    if (verified.isPresent()) {
      setHistory(verified.get());
    } else {
      throw assertionError("\n"
          + formatSection("expected called in order", invocationMatcher)
          + "  but not called\n"
          + formatCallings());
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
    getFacade().history.add(event);
  }

  private static boolean isMock(Object object) {
    return getFacade().isMock(object);
  }

  private static String formatSection(String caption, @Nullable Object content) {
    return ""
        + "  " + caption + "\n"
        + "    " + getFacade().formatter.format(content) + "\n";
  }

  private static String tryFormatDiagnosis(Object matcher, Object item) {
    Matcher asMatcher = asMatcher(matcher);
    return asMatcher instanceof DiagnosticMatcher
        ? formatSection("diagnosis", ((DiagnosticMatcher) asMatcher).diagnose(item))
        : "";
  }

  private static String formatCallings() {
    Facade facade = getFacade();
    StringBuilder builder = new StringBuilder();

    for (Object event : facade.history.get().reverse()) {
      if (event instanceof Calling) {
        Calling calling = (Calling) event;
        Invocation invocation = calling.invocation;
        builder.append("    ").append(facade.formatter.format(invocation)).append("\n");
      }
    }
    if (builder.length() > 0) {
      builder.insert(0, "  actual invocations\n");
    } else {
      builder.insert(0, "  actual invocations\n    none\n");
    }
    return builder.toString();
  }

  private static <T> T proxyWrapping(final T wrapped, final Handler handler) {
    Typing typing = typing(wrapped.getClass(), new HashSet<Class<?>>());
    Proxer proxer = getFacade().proxer;
    Handler proxyHandler = returningDefaultValue(delegatingTo(wrapped, handler));
    try {
      return (T) proxer.proxy(typing, proxyHandler);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

  private static Handler returningDefaultValue(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        handler.handle(invocation);
        return defaultValue(invocation.method.getReturnType());
      }
    };
  }

  private static Handler delegatingTo(final Object instance, final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return handler.handle(invocation(invocation.method, instance, invocation.arguments));
      }
    };
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
