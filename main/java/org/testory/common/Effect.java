package org.testory.common;

import static java.util.Objects.requireNonNull;

public abstract class Effect {
  private Effect() {}

  public static abstract class Returned extends Effect {
    private Returned() {}
  }

  public static class ReturnedObject extends Returned {
    @Nullable
    public final Object object;

    private ReturnedObject(Object object) {
      this.object = object;
    }

    public String toString() {
      return "returned(" + object + ")";
    }
  }

  public static class ReturnedVoid extends Returned {
    private ReturnedVoid() {}

    public String toString() {
      return "returnedVoid()";
    }
  }

  public static class Thrown extends Effect {
    public final Throwable throwable;

    private Thrown(Throwable throwable) {
      this.throwable = throwable;
    }

    public String toString() {
      return "thrown(" + throwable + ")";
    }
  }

  public static ReturnedObject returned(@Nullable Object object) {
    return new ReturnedObject(object);
  }

  public static Effect returnedVoid() {
    return new ReturnedVoid();
  }

  public static Thrown thrown(Throwable throwable) {
    return new Thrown(requireNonNull(throwable));
  }
}
