package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.hasMethod;

import org.junit.Before;
import org.junit.Test;

public class TestClassesHasMethod {
  private Class<?>[] parameters;
  private String name;
  private Class<?> type;

  @Before
  public void before() {
    parameters = new Class[] { Object.class };
    name = "method";
    type = Type.class;
  }

  @Test
  public void requires_same_name_and_parameters() {
    assertTrue(hasMethod(name, parameters, type));
  }

  @Test
  public void fails_for_different_type() {
    assertFalse(hasMethod(name, parameters, OtherType.class));
  }

  @Test
  public void fails_for_different_name() {
    assertFalse(hasMethod("otherName", parameters, type));
  }

  @Test
  public void fails_for_different_parameter_type() {
    assertFalse(hasMethod(name, new Class[] { String.class }, type));
  }

  @Test
  public void fails_for_more_parameters() {
    assertFalse(hasMethod(name, new Class[] { Object.class, Object.class }, type));
  }

  @Test
  public void fails_for_less_parameters() {
    assertFalse(hasMethod(name, new Class[0], type));
  }

  @Test
  public void name_cannot_be_null() {
    name = null;
    try {
      hasMethod(name, parameters, type);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void parameters_cannot_be_null() {
    parameters = null;
    try {
      hasMethod(name, parameters, type);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void parameters_cannot_contain_null() {
    parameters = new Class[] { null };
    try {
      hasMethod(name, parameters, type);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  private static class Type {
    @SuppressWarnings("unused")
    public void method(Object o) {}
  }

  private static class OtherType {}
}
