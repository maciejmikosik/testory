package org.testory.common;

import static org.testory.common.SequenceFormatter.sequence;

import java.lang.reflect.Array;
import java.util.Iterator;

public class ObjectFormatter implements Formatter {
  protected ObjectFormatter() {}

  public static Formatter objectFormatter() {
    return new ObjectFormatter();
  }

  public String format(@Nullable Object object) {
    if (object == null) {
      return "null";
    } else if (object.getClass().isArray()) {
      return formatArray(object);
    } else {
      return object.toString();
    }
  }

  private String formatArray(Object array) {
    return String.format("[%s]", sequence(", ", this).format(arrayAsIterable(array)));
  }

  private Iterable<Object> arrayAsIterable(final Object array) {
    return new Iterable<Object>() {
      public Iterator<Object> iterator() {
        return new Iterator<Object>() {
          private final int size = Array.getLength(array);
          int index = 0;

          public boolean hasNext() {
            return index < size;
          }

          public Object next() {
            return Array.get(array, index++);
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}
