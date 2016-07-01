package org.testory.plumbing;

public interface Maker {
  <T> T make(Class<T> type, String name);
}
