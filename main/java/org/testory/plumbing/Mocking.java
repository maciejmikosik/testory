package org.testory.plumbing;

import static org.testory.plumbing.Cache.newCache;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class Mocking {
  public final Object mock;
  public final String name;

  private Mocking(Object mock, String name) {
    this.mock = mock;
    this.name = name;
  }

  public static Mocking mocking(Object mock, String name) {
    check(mock != null);
    check(name != null);
    return new Mocking(mock, name);
  }

  public String toString() {
    return "mocking(" + mock + ", " + name + ")";
  }

  public static boolean isMock(Object mock, Chain<Object> history) {
    check(mock != null);
    check(history != null);
    Cache cache = localCache.get().update(history);
    localCache.set(cache);
    for (Object event : cache.untilLastTyped) {
      if (event instanceof Mocking) {
        Mocking mocking = (Mocking) event;
        if (mocking.mock == mock) {
          return true;
        }
      }
    }
    return false;
  }

  private static ThreadLocal<Cache> localCache = new ThreadLocal<Cache>() {
    protected Cache initialValue() {
      return newCache(Mocking.class);
    }
  };
}
