package org.testory.common;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Chain<E> implements Iterable<E> {
  private final int size;
  private final E element;
  private final Chain<E> tail;

  private Chain(int size, E element, Chain<E> tail) {
    this.tail = tail;
    this.size = size;
    this.element = element;
  }

  public static <E> Chain<E> chain() {
    return new Chain<E>(0, null, null);
  }

  public int size() {
    return size;
  }

  public E get() {
    checkHasElement();
    return element;
  }

  public Chain<E> add(E newElement) {
    return new Chain<E>(size + 1, requireNonNull(newElement), this);
  }

  public Chain<E> remove() {
    checkHasElement();
    return tail;
  }

  public Chain<E> reverse() {
    Chain<E> reversed = chain();
    for (E e : this) {
      reversed = reversed.add(e);
    }
    return reversed;
  }

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Chain<E> chain = Chain.this;

      public boolean hasNext() {
        return chain.hasElement();
      }

      public E next() {
        E nextElement = chain.get();
        chain = chain.tail;
        return nextElement;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public boolean equals(Object obj) {
    return obj instanceof Chain && equals((Chain<?>) obj);
  }

  private boolean equals(Chain<?> chain) {
    if (size != chain.size) {
      return false;
    }
    Chain<E> first = this;
    Chain<?> second = chain;
    while (first.size > 0) {
      if (first == second) {
        return true;
      }
      if (!Objects.equals(first.element, second.element)) {
        return false;
      }
      first = first.tail;
      second = second.tail;
    }
    return true;
  }

  public int hashCode() {
    int hash = 0xFFFF;
    Chain<E> chain = this;
    while (chain.size > 0) {
      hash += element.hashCode();
      hash *= 0xFFFF;
      chain = chain.tail;
    }
    return hash;
  }

  public String toString() {
    List<E> elements = new LinkedList<E>();
    Chain<E> chain = this;
    while (chain.size > 0) {
      elements.add(chain.element);
      chain = chain.tail;
    }
    return elements.toString();
  }

  private void checkHasElement() {
    if (!hasElement()) {
      throw new NoSuchElementException();
    }
  }

  private boolean hasElement() {
    return size != 0;
  }
}
