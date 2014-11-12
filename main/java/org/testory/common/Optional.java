package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

import java.util.NoSuchElementException;

public class Optional<T> {
  private final T value;

  private Optional(T value) {
    this.value = value;
  }

  public static <T> Optional<T> empty() {
    return new Optional<T>(null);
  }

  public boolean isPresent() {
    return value != null;
  }

  public T get() {
    if (value == null) {
      throw new NoSuchElementException();
    }
    return value;
  }

  public static <T> Optional<T> of(T value) {
    checkNotNull(value);
    return new Optional<T>(value);
  }
}
