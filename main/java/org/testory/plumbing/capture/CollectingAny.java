package org.testory.plumbing.capture;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Matcher;

public class CollectingAny {
  public final Matcher matcher;
  public final Object token;
  public final Object printable;

  private CollectingAny(Matcher matcher, Object token, Object printable) {
    this.matcher = matcher;
    this.token = token;
    this.printable = printable;
  }

  public static CollectingAny collectingAny(Matcher matcher, Object token, Object printable) {
    check(matcher != null);
    check(token != null);
    check(printable != null);
    return new CollectingAny(matcher, token, printable);
  }
}
