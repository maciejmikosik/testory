package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Matchers_anything {
  private Matcher matcher;

  @Before
  public void before() {
    matcher = Matchers.anything;
  }

  @Test
  public void matches_object() {
    assertTrue(matcher.matches(newObject("object")));
  }

  @Test
  public void matches_string() {
    assertTrue(matcher.matches("string"));
  }

  @Test
  public void matches_null() {
    assertTrue(matcher.matches(null));
  }

  @Test
  public void implements_to_string() {
    assertEquals("anything", matcher.toString());
  }
}
