package org.testory.plumbing.inject;

import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Array;

import org.testory.plumbing.Maker;

public class ArrayMaker implements Maker {
  private final Maker maker;

  private ArrayMaker(Maker maker) {
    this.maker = maker;
  }

  public static Maker singletonArray(Maker maker) {
    check(maker != null);
    return new ArrayMaker(maker);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    return type.isArray()
        ? makeArray(type, name)
        : maker.make(type, name);
  }

  private <T> T makeArray(Class<T> arrayType, String name) {
    Class<?> componentType = arrayType.getComponentType();
    Object array = Array.newInstance(componentType, 1);
    Object element = make(componentType, name + "[0]");
    Array.set(array, 0, element);
    return (T) array;
  }
}
