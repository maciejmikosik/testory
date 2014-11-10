package org.testory.plumbing;

import static org.testory.common.Collections.reverse;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.InvocationMatcher;
import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.util.Effect;
import org.testory.util.any.Any;

public class History {
  private List<Object> events = new ArrayList<Object>();

  public History() {}

  private List<Object> getEvents() {
    return new ArrayList<Object>(events);
  }

  private void setEvents(List<Object> events) {
    this.events = new ArrayList<Object>(events);
  }

  private void addEvent(Object event) {
    events.add(event);
  }

  private static class Purge {}

  public void purge() {
    List<Object> latestEvents = reverse(getEvents());
    for (int i = 0; i < latestEvents.size(); i++) {
      if (latestEvents.get(i) instanceof Purge) {
        setEvents(reverse(latestEvents.subList(0, i)));
        break;
      }
    }
    addEvent(new Purge());
  }

  public void purgeMark() {
    addEvent(new Purge());
  }

  public void purgeNow() {
    setEvents(new ArrayList<Object>());
  }

  public void logWhen(Effect effect) {
    check(effect != null);
    addEvent(effect);
  }

  public boolean hasLastWhenEffect() {
    return tryGetLastWhenEffect() != null;
  }

  public Effect getLastWhenEffect() {
    Effect effect = tryGetLastWhenEffect();
    check(effect != null);
    return effect;
  }

  @Nullable
  private Effect tryGetLastWhenEffect() {
    Effect effect = null;
    for (Object event : reverse(getEvents())) {
      if (event instanceof Effect) {
        effect = (Effect) event;
        break;
      }
    }
    return effect;
  }

  private static class Mocking {
    Object mock;
  }

  public void logMocking(Object mock) {
    check(mock != null);
    Mocking mocking = new Mocking();
    mocking.mock = mock;
    addEvent(mocking);
  }

  public boolean isMock(Object mock) {
    check(mock != null);
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
    InvocationMatcher invocationMatcher;
  }

  public void logStubbing(Handler handler, InvocationMatcher invocationMatcher) {
    Stubbing stubbing = new Stubbing();
    stubbing.handler = handler;
    stubbing.invocationMatcher = invocationMatcher;
    addEvent(stubbing);
  }

  public boolean hasStubbedHandlerFor(Invocation invocation) {
    return tryGetStubbedHandlerFor(invocation) != null;
  }

  public Handler getStubbedHandlerFor(Invocation invocation) {
    Handler handler = tryGetStubbedHandlerFor(invocation);
    check(handler != null);
    return handler;
  }

  @Nullable
  private Handler tryGetStubbedHandlerFor(Invocation invocation) {
    for (Object event : reverse(getEvents())) {
      if (event instanceof Stubbing) {
        Stubbing stubbing = (Stubbing) event;
        if (stubbing.invocationMatcher.matches(invocation)) {
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
    return invocations;
  }

  public void logAny(Any any) {
    addEvent(any);
  }

  public List<Any> getAnysAndConsume() {
    class Consumer {}
    List<Any> anys = new ArrayList<Any>();
    for (Object event : reverse(getEvents())) {
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
