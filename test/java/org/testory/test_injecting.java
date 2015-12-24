package org.testory;

import static java.util.Objects.deepEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.willReturn;
import static org.testory.common.Samples.sample;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.Reflections.readDeclaredFields;

import java.lang.annotation.ElementType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class test_injecting {
  private final String string = "string";
  private List<Object> fields;

  @Test
  public void injects_mock_of_concrete_class() {
    class ConcreteClass {}
    class TestClass {
      @SuppressWarnings("unused")
      ConcreteClass a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsMocks(test);
  }

  @Test
  public void injects_mock_of_interface() {
    class TestClass {
      @SuppressWarnings("unused")
      Interface a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsMocks(test);
  }

  @Test
  public void injects_mock_of_abstract_class() {
    abstract class AbstractClass {}
    class TestClass {
      @SuppressWarnings("unused")
      AbstractClass a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsMocks(test);
  }

  @Test
  public void injects_mock_of_object() {
    class TestClass {
      @SuppressWarnings("unused")
      Object a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsMocks(test);
  }

  @Test
  public void injects_array_of_mocks() {
    class TestClass {
      Object[] objects;
      Object[][] deepObjects;
    }
    TestClass test = new TestClass();
    givenTest(test);

    assertEquals(1, test.objects.length);
    assertNotNull(test.objects[0]);
    assertTrue(test.objects[0].equals(test.objects[0]));
    assertEquals("objects[0]", test.objects[0].toString());

    assertEquals(1, test.deepObjects.length);
    assertEquals(1, test.deepObjects[0].length);
    assertNotNull(test.deepObjects[0][0]);
    assertTrue(test.deepObjects[0][0].equals(test.deepObjects[0][0]));
    assertEquals("deepObjects[0][0]", test.deepObjects[0][0].toString());
  }

  @Test
  public void mock_to_string_is_prestubbed() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field.toString());
  }

  @Test
  public void mock_equals_is_prestubbed() {
    class TestClass {
      Object field, otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field.equals(test.field));
    assertFalse(test.field.equals(test.otherField));
    assertFalse(test.otherField.equals(test.field));
    assertFalse(test.field.equals(null));
  }

  @Test
  public void mock_hashcode_is_prestubbed() {
    class TestClass {
      Object field, otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(test.field.hashCode(), test.field.hashCode());
    assertTrue(test.field.hashCode() != test.otherField.hashCode());
  }

  @Test
  public void mock_is_stubbable() {
    class TestClass {
      List<Object> field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    given(willReturn(string), onInstance(test.field));
    assertEquals(string, test.field.get(0));
  }

  @Test
  public void mock_is_restubbable() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    given(willReturn(string), onInstance(test.field));
    assertEquals(string, test.field.toString());
  }

  @Test
  public void injects_sample_boolean() {
    class TestClass {
      @SuppressWarnings("unused")
      boolean a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Boolean() {
    class TestClass {
      @SuppressWarnings("unused")
      boolean a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_char() {
    class TestClass {
      @SuppressWarnings("unused")
      char a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Character() {
    class TestClass {
      @SuppressWarnings("unused")
      Character a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_byte() {
    class TestClass {
      @SuppressWarnings("unused")
      byte a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Byte() {
    class TestClass {
      @SuppressWarnings("unused")
      Byte a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_short() {
    class TestClass {
      @SuppressWarnings("unused")
      short a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Short() {
    class TestClass {
      @SuppressWarnings("unused")
      Short a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_int() {
    class TestClass {
      @SuppressWarnings("unused")
      int a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Integer() {
    class TestClass {
      @SuppressWarnings("unused")
      Integer a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_long() {
    class TestClass {
      @SuppressWarnings("unused")
      long a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Long() {
    class TestClass {
      @SuppressWarnings("unused")
      Long a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_float() {
    class TestClass {
      @SuppressWarnings("unused")
      float a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Float() {
    class TestClass {
      @SuppressWarnings("unused")
      Float a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_double() {
    class TestClass {
      @SuppressWarnings("unused")
      double a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Double() {
    class TestClass {
      @SuppressWarnings("unused")
      Double a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_string() {
    class TestClass {
      @SuppressWarnings("unused")
      String a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Class() {
    class TestClass {
      @SuppressWarnings("unused")
      Class<?> a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Field() {
    class TestClass {
      @SuppressWarnings("unused")
      Field a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Method() {
    class TestClass {
      @SuppressWarnings("unused")
      Method a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_Constructor() {
    class TestClass {
      @SuppressWarnings("unused")
      Constructor<?> a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void injects_sample_boolean_array() {
    class TestClass {
      @SuppressWarnings("unused")
      boolean[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Boolean_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Boolean[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_char_array() {
    class TestClass {
      @SuppressWarnings("unused")
      char[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Character_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Character[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_byte_array() {
    class TestClass {
      @SuppressWarnings("unused")
      byte[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Byte_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Byte[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_short_array() {
    class TestClass {
      @SuppressWarnings("unused")
      short[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Short_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Short[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_int_array() {
    class TestClass {
      @SuppressWarnings("unused")
      int[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Integer_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Integer[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_long_array() {
    class TestClass {
      @SuppressWarnings("unused")
      long[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Long_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Long[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_float_array() {
    class TestClass {
      @SuppressWarnings("unused")
      float[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Float_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Float[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_double_array() {
    class TestClass {
      @SuppressWarnings("unused")
      double[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_Double_array() {
    class TestClass {
      @SuppressWarnings("unused")
      Double[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_String_array() throws IllegalAccessException {
    @SuppressWarnings("unused")
    class TestClass {
      String[] a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainArraysWithSamples(test);
  }

  @Test
  public void injects_sample_deep_String_array() {
    class TestClass {
      String[][] deepArray;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertArrayEquals(new String[][] { { sample(String.class, "deepArray[0][0]") } },
        test.deepArray);
  }

  @Test
  public void injects_sample_enum() {
    @SuppressWarnings("unused")
    class TestClass {
      ElementType a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertContainsSamples(test);
  }

  @Test
  public void skips_not_null() {
    @SuppressWarnings("unused")
    class TestClass {
      Object field = new Object();
      String stringField = "value";
      Boolean booleanField = Boolean.TRUE;
      Character characterField = Character.valueOf((char) 1);
      Byte byteField = Byte.valueOf((byte) 1);
      Short shortField = Short.valueOf((short) 1);
      Integer integerField = Integer.valueOf(1);
      Long longField = Long.valueOf(1);
      Float floatField = Float.valueOf(1);
      Double doubleField = Double.valueOf(1);
    }
    TestClass test = new TestClass();
    fields = readDeclaredFields(test);
    givenTest(test);
    assertEquals(fields, readDeclaredFields(test));
  }

  @Test
  public void skips_void() {
    class TestClass {
      Void voidField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(null, test.voidField);
  }

  @Test
  public void skips_primitive_not_equal_to_binary_zero() {
    @SuppressWarnings("unused")
    class TestClass {
      boolean booleanPrimitive = true;
      boolean booleanWrapper = true;
      char charField = 'a';
      byte byteField = 1;
      short shortField = 1;
      int intField = 1;
      long longField = 1;
      float floatField = 1;
      double doubleField = 1;
    }
    TestClass test = new TestClass();
    fields = readDeclaredFields(test);
    givenTest(test);
    assertEquals(fields, readDeclaredFields(test));
  }

  static class TestClassWithStaticField {
    static Object field = null;
  }

  @Test
  public void skips_static_field() {
    TestClassWithStaticField test = new TestClassWithStaticField();
    givenTest(test);
    assertNull(TestClassWithStaticField.field);
  }

  @Test
  public void skips_final_field() {
    class TestClass {
      final Object field = null;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertNull(test.field);
  }

  @Test
  public void cannot_inject_final_class() {
    final class FinalClass {}
    class TestClass {
      @SuppressWarnings("unused")
      FinalClass fieldOfFinalClass;
    }
    TestClass test = new TestClass();
    try {
      givenTest(test);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessageContaining("fieldOfFinalClass"));
    }
  }

  private static void assertContainsMocks(Object instance) {
    try {
      for (Field field : injectableFields(instance.getClass())) {
        Object mock = field.get(instance);
        assertTrue(field.getType().isInstance(mock));
        assertNotSame(field.getType(), mock.getClass());
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static void assertContainsSamples(Object instance) {
    try {
      for (Field field : injectableFields(instance.getClass())) {
        assertEquals(sample(field.getType(), field.getName()), field.get(instance));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static void assertContainArraysWithSamples(Object instance) {
    try {
      for (Field field : injectableFields(instance.getClass())) {
        Object array = Array.newInstance(field.getType().getComponentType(), 1);
        Array.set(array, 0, sample(field.getType().getComponentType(), field.getName() + "[0]"));
        assertTrue(deepEquals(field.get(instance), array));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }

  private static List<Field> injectableFields(Class<?> type) {
    List<Field> fields = new ArrayList<>();
    for (Field field : type.getDeclaredFields()) {
      if (!field.isSynthetic()) {
        fields.add(field);
      }
    }
    return fields;
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }
    };
  }

  public static interface Interface {}
}
