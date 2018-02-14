package org.testory.plumbing.format;

import org.testory.common.Nullable;

public class Header {
  public final Object object;

  private Header(Object object) {
    this.object = object;
  }

  public static Object header(@Nullable Object object) {
    return new Header(object);
  }
}
