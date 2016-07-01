package org.testory.plumbing;

import static org.testory.plumbing.Cache.newCache;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Purging.purge;

import java.util.ArrayList;
import java.util.List;

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

  public static boolean isMock(Object mock, History history) {
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

  public static String nameMock(Class<?> type, History history) {
    List<String> mockNames = mockNames(history);
    String typeName = type.getSimpleName();
    for (int i = 0;; i++) {
      String name = "mock" + typeName + i;
      if (!mockNames.contains(name)) {
        return name;
      }
    }
  }

  private static List<String> mockNames(History history) {
    List<String> mockNames = new ArrayList<String>();
    for (Object event : purge(history).events) {
      if (event instanceof Mocking) {
        Mocking mocking = (Mocking) event;
        mockNames.add(mocking.name);
      }
    }
    return mockNames;
  }

  private static ThreadLocal<Cache> localCache = new ThreadLocal<Cache>() {
    protected Cache initialValue() {
      return newCache(Mocking.class);
    }
  };
}
