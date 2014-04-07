package org.testory.common;

import java.util.ArrayList;
import java.util.List;

public class Collections {
  public static <E> E last(List<E> list) {
    return list.get(list.size() - 1);
  }

  public static <E> List<E> flip(List<E> list) {
    List<E> newList = new ArrayList<E>(list);
    java.util.Collections.reverse(newList);
    return newList;
  }
}
