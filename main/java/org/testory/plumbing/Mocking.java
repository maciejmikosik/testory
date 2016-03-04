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

  public static String nameMock(Object mock, History history) {
    List<String> mockNames = mockNames(history);
    String mockedType = mockedType(mock);
    for (int i = 0;; i++) {
      String name = "mock" + mockedType + i;
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

  private static String mockedType(Object mock) {
    String name = mock.getClass().getSimpleName();
    return hideProxiable(removeInnerClassIndex(removeOuterClass(removeCglib(name))));
  }

  private static String removeCglib(String name) {
    return name.split("\\$\\$")[0];
  }

  private static String removeOuterClass(String name) {
    String[] split = name.split("\\$");
    return split[split.length - 1];
  }

  private static String removeInnerClassIndex(String name) {
    char[] chars = name.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (!Character.isDigit(chars[i])) {
        return String.valueOf(chars, i, chars.length - i);
      }
    }
    return name;
  }

  private static String hideProxiable(String name) {
    return name.equals("ProxiableObject")
        ? "Object"
        : name;
  }

  private static ThreadLocal<Cache> localCache = new ThreadLocal<Cache>() {
    protected Cache initialValue() {
      return newCache(Mocking.class);
    }
  };
}
