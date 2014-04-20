package org.testory.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collections {
  public static <E> E last(List<E> list) {
    return list.get(list.size() - 1);
  }

  public static <E> List<E> flip(List<E> list) {
    List<E> newList = new ArrayList<E>(list);
    java.util.Collections.reverse(newList);
    return newList;
  }

  public static <E> List<E> immutable(List<? extends E> list) {
    return java.util.Collections.unmodifiableList(new ArrayList<E>(list));
  }

  public static <E> Set<E> immutable(Set<? extends E> set) {
    return java.util.Collections.unmodifiableSet(new HashSet<E>(set));
  }
}
