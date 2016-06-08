package org.testory.common;

public interface Sampler {
  <T> T sample(Class<T> type, String name);
}
