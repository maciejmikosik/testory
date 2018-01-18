package org.testory.facade;

import static java.util.Objects.requireNonNull;

import org.testory.common.Formatter;
import org.testory.plumbing.history.History;

@SuppressWarnings("hiding")
public class Configuration {
  public final History history;
  public final Formatter formatter;
  public final Class<? extends RuntimeException> exception;

  private Configuration(
      History history,
      Formatter formatter,
      Class<? extends RuntimeException> exception) {
    this.history = history;
    this.formatter = formatter;
    this.exception = exception;
  }

  public static Configuration configuration() {
    return new Configuration(
        null,
        null,
        null);
  }

  public Configuration history(History history) {
    return new Configuration(
        requireNonNull(history),
        formatter,
        exception);
  }

  public Configuration formatter(Formatter formatter) {
    return new Configuration(
        history,
        requireNonNull(formatter),
        exception);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    return new Configuration(
        history,
        formatter,
        requireNonNull(exception));
  }

  public Configuration validate() {
    requireNonNull(history);
    requireNonNull(formatter);
    requireNonNull(exception);
    return this;
  }
}
