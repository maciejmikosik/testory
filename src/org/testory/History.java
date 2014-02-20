package org.testory;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.zeroOrNull;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;
import static org.testory.util.Matchers.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;
import org.testory.util.Effect;

class History {
  /** youngest at begin */
  private final ThreadLocal<List<Object>> threadLocalEvents = new ThreadLocal<List<Object>>() {
    protected List<Object> initialValue() {
      return new ArrayList<Object>();
    }
  };

  public History() {}

  private List<Object> getEvents() {
    return new ArrayList<Object>(threadLocalEvents.get());
  }

  private void setEvents(List<Object> events) {
    threadLocalEvents.set(new ArrayList<Object>(events));
  }

  private void addEvent(Object event) {
    threadLocalEvents.get().add(0, event);
  }

  private static class Purge {}

  public void purge() {
    List<Object> events = getEvents();
    for (int i = 0; i < events.size(); i++) {
      if (events.get(i) instanceof Purge) {
        setEvents(events.subList(0, i));
        break;
      }
    }
    addEvent(new Purge());
  }

  public void logWhen(Effect effect) {
    checkNotNull(effect);
    addEvent(effect);
  }

  public Effect getLastWhenEffect() {
    Effect effect = null;
    for (Object event : getEvents()) {
      if (event instanceof Effect) {
        effect = (Effect) event;
        break;
      }
    }
    check(effect != null);
    return effect;
  }

  private static void check(boolean condition) {
    if (!condition) {
      throw new TestoryException();
    }
  }

  private static class Mocking {
    Object mock;
  }

  public void logMocking(Object mock) {
    checkNotNull(mock);
    Mocking mocking = new Mocking();
    mocking.mock = mock;
    addEvent(mocking);
  }

  public boolean isMock(Object mock) {
    checkNotNull(mock);
    for (Object event : getEvents()) {
      if (event instanceof Mocking) {
        Mocking mocking = (Mocking) event;
        if (mocking.mock == mock) {
          return true;
        }
      }
    }
    return false;
  }

  private static class Stubbing {
    Handler handler;
    Captor captor;
  }

  public void logStubbing(Handler handler, Captor captor) {
    Stubbing stubbing = new Stubbing();
    stubbing.handler = handler;
    stubbing.captor = captor;
    addEvent(stubbing);
  }

  public boolean hasStubbedHandlerFor(Invocation invocation) {
    return tryGetStubbedHandlerFor(invocation) != null;
  }

  public Handler getStubbedHandlerFor(Invocation invocation) {
    Handler handler = tryGetStubbedHandlerFor(invocation);
    checkArgument(handler != null);
    return handler;
  }

  @Nullable
  private Handler tryGetStubbedHandlerFor(Invocation invocation) {
    for (Object event : getEvents()) {
      if (event instanceof Stubbing) {
        Stubbing stubbing = (Stubbing) event;
        if (stubbing.captor.matches(invocation)) {
          return stubbing.handler;
        }
      }
    }
    return null;
  }

  public void logInvocation(Invocation invocation) {
    addEvent(invocation);
  }

  public List<Invocation> getInvocations() {
    List<Invocation> invocations = new ArrayList<Invocation>();
    for (Object event : getEvents()) {
      if (event instanceof Invocation) {
        invocations.add((Invocation) event);
      }
    }
    return Collections.unmodifiableList(invocations);
  }

  private static class Any {
    Class<?> type;
    @Nullable
    Object token;
    @Nullable
    Object matcher;
  }

  public <T> T logAny(Class<T> type, @Nullable Object matcher) {
    boolean isProxiable = isProxiable(type);
    T token = isProxiable
        ? proxyLight(type)
        : null;

    Any any = new Any();
    any.type = type;
    any.token = token;
    any.matcher = matcher;
    addEvent(any);

    return isProxiable
        ? token
        : zeroOrNull(type);
  }

  private static <T> T proxyLight(Class<T> type) {
    Typing typing = type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return null;
      }
    };
    return (T) proxy(typing, handler);
  }

  public Captor buildCaptorUsingAnys(final Invocation invocation) {
    final List<Any> anys = getAnysAndConsume();
    final List<Object> argumentMatchers = solve(invocation.arguments, anys);
    final Object argumentsMatcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        List<Object> arguments = (List<Object>) item;
        for (int i = 0; i < argumentMatchers.size(); i++) {
          if (!match(argumentMatchers.get(i), arguments.get(i))) {
            return false;
          }
        }
        return true;
      }
    };

    return new Captor() {
      public boolean matches(Invocation item) {
        return invocation.instance == item.instance && invocation.method.equals(item.method)
            && match(argumentsMatcher, item.arguments);
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dangerouslyInvokeToStringOnMock(invocation.instance));
        builder.append(".");
        builder.append(invocation.method.getName());
        builder.append("(");
        for (Object argumentMatcher : argumentMatchers) {
          builder.append(argumentMatcher).append(", ");
        }
        if (argumentMatchers.size() > 0) {
          builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")");
        return builder.toString();
      }
    };
  }

  private static List<Object> solve(List<Object> arguments, List<Any> anys) {
    for (int i = 0; i < anys.size(); i++) {
      Any any = anys.get(i);
      if (any.token != null) {
        for (int j = 0; j < arguments.size(); j++) {
          Object argument = arguments.get(j);
          if (argument == any.token) {
            ArrayList<Object> solved = new ArrayList<Object>();
            List<Object> leftArguments = arguments.subList(0, j);
            List<Object> rightArguments = arguments.subList(j + 1, arguments.size());
            List<Any> leftAnys = anys.subList(0, i);
            List<Any> rightAnys = anys.subList(i + 1, anys.size());
            solved.addAll(solve(leftArguments, leftAnys));
            solved.add(asMatcher(any));
            solved.addAll(solve(rightArguments, rightAnys));
            return solved;
          }
        }
        throw new TestoryException("any() created but not passed to invocation");
      }
    }
    if (arguments.isEmpty()) {
      return new ArrayList<Object>();
    }
    if (anys.isEmpty()) {
      ArrayList<Object> solved = new ArrayList<Object>();
      for (final Object argument : arguments) {
        solved.add(new Object() {
          @SuppressWarnings("unused")
          public boolean matches(Object item) {
            return areEqualDeep(argument, item);
          }

          public String toString() {
            return print(argument);
          }
        });
      }
      return solved;
    } else if (anys.size() == arguments.size()) {
      ArrayList<Object> solved = new ArrayList<Object>();
      for (Any any : anys) {
        solved.add(asMatcher(any));
      }
      return solved;
    } else {
      throw new TestoryException("cannot solve mixed arguments and anys");
    }
  }

  private static Object asMatcher(final Any any) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return any.matcher != null
            ? match(any.matcher, item)
            : true;
      }

      public String toString() {
        return any.matcher != null
            ? "any(" + any.type.getName() + ", " + any.matcher + ")"
            : "any(" + any.type.getName() + ")";
      }
    };
  }

  /**
   * Invoked only in specific situation if we know that test is failing.
   * {@link Testory#thenCalledTimes(Object, Captor)} assertion fails, it builds error message, it
   * invokes {@link Captor#toString()}, it invokes this method, it invokes mock.toString(). Invoking
   * in other situations may cause not intended consequences like extra invocation to be registered
   * and verification fails.
   */

  private static String dangerouslyInvokeToStringOnMock(Object mock) {
    return mock.toString();
  }

  private List<Any> getAnysAndConsume() {
    class Consumer {}
    List<Any> anys = new ArrayList<Any>();
    for (Object event : getEvents()) {
      if (event instanceof Any) {
        anys.add(0, (Any) event);
      } else if (event instanceof Consumer) {
        break;
      }
    }
    addEvent(new Consumer());
    return anys;
  }
}
