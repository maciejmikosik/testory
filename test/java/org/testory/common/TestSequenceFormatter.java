package org.testory.common;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.SequenceFormatter.sequence;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestSequenceFormatter {
  private Formatter formatter;
  private SequenceFormatter sequenceFormatter;
  private String separator;
  private Object a, b, c;
  private String fA, fB, fC;

  @Before
  public void before() {
    separator = "separator";
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    fA = "fA";
    fB = "fB";
    fC = "fC";
    formatter = new Formatter() {
      public String format(Object object) {
        if (object == a) {
          return fA;
        } else if (object == b) {
          return fB;
        } else if (object == c) {
          return fC;
        } else {
          return null;
        }
      }
    };
  }

  @Test
  public void formats_elements() {
    sequenceFormatter = sequence(separator, formatter);
    assertEquals(
        formatter.format(a) + separator + formatter.format(b) + separator + formatter.format(c),
        sequenceFormatter.format(asIterable(asList(a, b, c))));
  }

  @Test
  public void formats_single_element() {
    sequenceFormatter = sequence(separator, formatter);
    assertEquals(
        formatter.format(a),
        sequenceFormatter.format(asIterable(asList(a))));
  }

  @Test
  public void formats_no_elements() {
    sequenceFormatter = sequence(separator, formatter);
    assertEquals(
        "",
        sequenceFormatter.format(asIterable(emptyList())));
  }

  @Test
  public void implements_to_string() {
    sequenceFormatter = sequence(separator, formatter);
    assertEquals(
        format("sequence(%s, %s)", separator, formatter),
        sequenceFormatter.toString());
  }

  @Test
  public void list_cannot_be_null() {
    sequenceFormatter = sequence(separator, formatter);
    try {
      sequenceFormatter.format(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void separator_cannot_be_null() {
    separator = null;
    try {
      sequence(separator, formatter);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void formatter_cannot_be_null() {
    formatter = null;
    try {
      sequence(separator, formatter);
      fail();
    } catch (NullPointerException e) {}
  }

  private static <E> Iterable<E> asIterable(Iterable<E> iterable) {
    return iterable;
  }
}
