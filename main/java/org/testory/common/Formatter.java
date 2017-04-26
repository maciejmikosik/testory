package org.testory.common;

import static java.lang.String.join;

import java.lang.reflect.Array;
import java.util.Iterator;

public class Formatter {
  protected Formatter() {}

  public static Formatter formatter() {
    return new Formatter();
  }

  public String format(@Nullable Object object) {
    if (object == null) {
      return "null";
    } else if (object.getClass().isArray()) {
      return formatArray(object);
    } else {
      return String.valueOf(object);
    }
  }

  public String formatSequence(final Iterable<?> iterable) {
    Iterable<String> strings = new Iterable<String>() {
      public Iterator<String> iterator() {
        return new Iterator<String>() {
          Iterator<?> iterator = iterable.iterator();

          public boolean hasNext() {
            return iterator.hasNext();
          }

          public String next() {
            return format(iterator.next());
          }
        };
      }
    };
    return join(", ", strings);
  }

  private String formatArray(Object array) {
    return "[" + formatSequence(arrayAsIterable(array)) + "]";
  }

  private Iterable<Object> arrayAsIterable(final Object array) {
    return new Iterable<Object>() {
      public Iterator<Object> iterator() {
        return new Iterator<Object>() {
          final int size = Array.getLength(array);
          int index = 0;

          public boolean hasNext() {
            return index < size;
          }

          public Object next() {
            return Array.get(array, index++);
          }
        };
      }
    };
  }
}
