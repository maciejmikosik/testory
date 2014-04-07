package org.testory.common;

import java.util.List;

public class Collections {
  public static <E> E last(List<E> list) {
    return list.get(list.size() - 1);
  }
}
