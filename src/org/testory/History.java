package org.testory;

import static org.testory.common.Checks.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
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

  public void logWhen(Effect effect) {
    checkNotNull(effect);

    List<Object> events = getEvents();
    for (int i = 0; i < events.size(); i++) {
      if (events.get(i) instanceof Effect) {
        setEvents(events.subList(0, i));
      }
    }
    addEvent(effect);
  }

  public Effect getLastWhenEffect() {
    Effect effect = null;
    for (Object event : getEvents()) {
      if (event instanceof Effect) {
        effect = (Effect) event;
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

  private static class Stubbing {
    Handler handler;
    Invocation invocation;
  }

  public void logStubbing(Handler handler, Invocation invocation) {
    Stubbing stubbing = new Stubbing();
    stubbing.handler = handler;
    stubbing.invocation = invocation;
    addEvent(stubbing);
  }

  public Handler getStubbedHandlerFor(Invocation invocation) {
    for (Object event : getEvents()) {
      if (event instanceof Stubbing) {
        Stubbing stubbing = (Stubbing) event;
        if (stubbing.invocation.equals(invocation)) {
          return stubbing.handler;
        }
      }
    }
    return null;
  }
}
