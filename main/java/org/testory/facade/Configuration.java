package org.testory.facade;

import static java.util.Objects.requireNonNull;

import org.testory.common.Formatter;
import org.testory.plumbing.history.History;

@SuppressWarnings("hiding")
public class Configuration {
  public final History history;
  public final Formatter formatter;

  private Configuration(
      History history,
      Formatter formatter) {
    this.history = history;
    this.formatter = formatter;
  }

  public static Configuration configuration() {
    return new Configuration(
        null,
        null);
  }

  public Configuration history(History history) {
    return new Configuration(
        requireNonNull(history),
        formatter);
  }

  public Configuration formatter(Formatter formatter) {
    return new Configuration(
        history,
        requireNonNull(formatter));
  }

  public Configuration validate() {
    requireNonNull(history);
    requireNonNull(formatter);
    return this;
  }
}
