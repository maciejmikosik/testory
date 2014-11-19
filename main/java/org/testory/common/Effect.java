package org.testory.common;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

public class Effect {
  private static enum Mode {
    returnedObject, returnedVoid, thrown
  }

  private final Mode mode;
  private final Object returned;
  private final Throwable thrown;

  private Effect(Mode mode, Object returned, Throwable thrown) {
    this.mode = mode;
    this.returned = returned;
    this.thrown = thrown;
  }

  public static Effect returned(@Nullable Object object) {
    return new Effect(Mode.returnedObject, object, null);
  }

  public static Effect returnedVoid() {
    return new Effect(Mode.returnedVoid, null, null);
  }

  public static Effect thrown(Throwable throwable) {
    checkNotNull(throwable);
    return new Effect(Mode.thrown, null, throwable);
  }

  public static boolean hasReturned(Effect effect) {
    return effect.mode == Mode.returnedObject || effect.mode == Mode.returnedVoid;
  }

  public static boolean hasReturnedObject(Effect effect) {
    return effect.mode == Mode.returnedObject;
  }

  public static boolean hasReturnedVoid(Effect effect) {
    return effect.mode == Mode.returnedVoid;
  }

  public static boolean hasThrown(Effect effect) {
    return effect.mode == Mode.thrown;
  }

  @Nullable
  public static Object getReturned(Effect effect) {
    checkArgument(effect.mode == Mode.returnedObject);
    return effect.returned;
  }

  public static Throwable getThrown(Effect effect) {
    checkArgument(effect.mode == Mode.thrown);
    return effect.thrown;
  }
}
