package org.testory.facade;

import static java.util.Objects.requireNonNull;

import org.testory.common.Formatter;
import org.testory.plumbing.history.History;
import org.testory.proxy.Proxer;

@SuppressWarnings("hiding")
public class Configuration {
  public final History history;
  public final Formatter formatter;
  public final Class<? extends RuntimeException> exception;
  public final Proxer proxer;

  private Configuration(
      History history,
      Formatter formatter,
      Class<? extends RuntimeException> exception,
      Proxer proxer) {
    this.history = history;
    this.formatter = formatter;
    this.exception = exception;
    this.proxer = proxer;
  }

  public static Configuration configuration() {
    return new Configuration(
        null,
        null,
        null,
        null);
  }

  public Configuration history(History history) {
    return new Configuration(
        requireNonNull(history),
        formatter,
        exception,
        proxer);
  }

  public Configuration formatter(Formatter formatter) {
    return new Configuration(
        history,
        requireNonNull(formatter),
        exception,
        proxer);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    return new Configuration(
        history,
        formatter,
        requireNonNull(exception),
        proxer);
  }

  public Configuration proxer(Proxer proxer) {
    return new Configuration(
        history,
        formatter,
        exception,
        requireNonNull(proxer));
  }

  public Configuration validate() {
    requireNonNull(history);
    requireNonNull(formatter);
    requireNonNull(exception);
    requireNonNull(proxer);
    return this;
  }
}
