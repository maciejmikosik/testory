package org.testory.plumbing.format;

import org.testory.common.Nullable;

public class Body {
  public final Object object;

  private Body(Object object) {
    this.object = object;
  }

  public static Object body(@Nullable Object object) {
    return new Body(object);
  }
}
