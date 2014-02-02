package org.testory.util;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Primitives {
  public static Object zeroOrNull(Class<?> type) {
    checkNotNull(type);
    checkArgument(!type.isPrimitive());
    return zeroes.get(type);
  }

  private static final Map<Class<?>, Object> zeroes = zeroes();

  private static Map<Class<?>, Object> zeroes() {
    Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
    map.put(Boolean.class, false);
    map.put(Character.class, Character.valueOf((char) 0));
    map.put(Byte.class, Byte.valueOf((byte) 0));
    map.put(Short.class, Short.valueOf((short) 0));
    map.put(Integer.class, Integer.valueOf(0));
    map.put(Long.class, Long.valueOf(0));
    map.put(Float.class, Float.valueOf(0));
    map.put(Double.class, Double.valueOf(0));
    return Collections.unmodifiableMap(map);
  }
}
