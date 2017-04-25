package org.testory.plumbing.inject;

import static java.lang.String.format;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.testory.plumbing.Maker;

public class TestingMakers {
  public static void assertFairDistribution(int expectedUniqueElements, List<?> elements) {
    Map<Object, Integer> histogram = histogram(elements);
    if (histogram.size() < expectedUniqueElements) {
      fail(format("there was %s unique elements, expected at least %s",
          histogram.size(), expectedUniqueElements));
    }
    float averageCount = 1f * elements.size() / histogram.size();
    for (float frequency : histogram.values()) {
      assertThat(frequency, withinRange(
          averageCount / 3,
          averageCount * 3));
    }
  }

  private static Map<Object, Integer> histogram(List<?> elements) {
    Map<Object, Integer> histogram = new HashMap<>();
    for (Object element : elements) {
      histogram.put(element, zeroIfNull(histogram.get(element)) + 1);
    }
    return histogram;
  }

  private static int zeroIfNull(Integer value) {
    return value == null
        ? 0
        : value;
  }

  public static <T extends Comparable<T>> Matcher<T> withinRange(final T lower, final T upper) {
    return new TypeSafeMatcher<T>() {
      public void describeTo(Description description) {
        description.appendText(format("within range [%s, %s]", lower, upper));
      }

      protected boolean matchesSafely(T item) {
        return lower.compareTo(item) <= 0 && upper.compareTo(item) >= 0;
      }
    };
  }

  public static <T> List<T> population(int count, Maker sampler, Class<T> type, String namePrefix) {
    List<T> samples = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      samples.add(sampler.make(type, namePrefix + i));
    }
    return samples;
  }
}
