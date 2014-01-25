package org.testory;

import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Invocations.on;
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

  private static Object mockOrSample(Class<?> type, final String name) {
    if (isProxiable(type)) {
      Typing typing = type.isInterface()
          ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
          : typing(type, new HashSet<Class<?>>());
      Handler handler = new Handler() {
        public Object handle(Invocation invocation) {
          if (invocation.method.getName().equals("toString")) {
            return name;
          }
          if (invocation.method.getName().equals("equals") && invocation.arguments.size() == 1) {
            return invocation.instance == invocation.arguments.get(0);
          }
          if (invocation.method.getName().equals("hashCode") && invocation.arguments.size() == 0) {
            return name.hashCode();
          }
          return null;
        }
      };
      return proxy(typing, handler);
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

  public static <T> T givenTry(final T object) {
    check(object != null);
    check(isProxiable(object.getClass()));
    Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        Invocation onObjectInvocation = on(object, invocation);
        try {
          return invoke(onObjectInvocation);
        } catch (Throwable e) {
          return null;
        }
      }
    };
    return (T) proxy(typing, handler);
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

  public static <T> T givenTimes(final int number, final T object) {
    check(number >= 0);
    check(object != null);
    check(isProxiable(object.getClass()));
    Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
    Handler handler = new Handler() {
      public Object handle(final Invocation invocation) throws Throwable {
        final Invocation onObjectInvocation = on(object, invocation);
        for (int i = 0; i < number; i++) {
          invoke(onObjectInvocation);
        }
        return null;
      }
    };
    return (T) proxy(typing, handler);
  }

  public static <T> T when(final T object) {
    history.logWhen(returned(object));
    boolean isProxiable = object != null && isProxiable(object.getClass());
    if (isProxiable) {
      Typing typing = typing(object.getClass(), new HashSet<Class<?>>());
      Handler handler = new Handler() {
        public Object handle(final Invocation invocation) {
          history.logWhen(effectOfInvoke(on(object, invocation)));
          return null;
        }
      };
      return (T) proxy(typing, handler);
    } else {
      return null;
    }
  }

  public static void when(Closure closure) {
    check(closure != null);
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
