package org.testory.plumbing.inject;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.deepEquals;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Classes.setAccessible;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Field;

import org.testory.plumbing.Maker;

public class Injector {
  private final Maker maker;

  private Injector(Maker maker) {
    this.maker = maker;
  }

  public static Injector injector(Maker maker) {
    check(maker != null);
    return new Injector(maker);
  }

  public void inject(Object test) {
    check(test != null);
    try {
      for (Field field : test.getClass().getDeclaredFields()) {
        if (!isStatic(field.getModifiers()) && !isFinal(field.getModifiers())) {
          setAccessible(field);
          if (deepEquals(defaultValue(field.getType()), field.get(test))) {
            field.set(test, maker.make(field.getType(), field.getName()));
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new Error(e);
    }
  }
}
