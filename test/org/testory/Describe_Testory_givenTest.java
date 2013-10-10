package org.testory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.givenTest;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

public class Describe_Testory_givenTest {
  @Test
  public void should_inject_concrete_class() {
    class ConcreteClass {}
    class TestClass {
      ConcreteClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof ConcreteClass);
  }

  @Test
  public void should_inject_interface() {
    class TestClass {
      Interface field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Interface);
  }

  @Test
  public void should_inject_abstract_class() {
    abstract class AbstractClass {}
    class TestClass {
      AbstractClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof AbstractClass);
  }

  @Test
  public void should_inject_object_class() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Object);
  }

  @Test
  public void should_stub_to_string_to_return_field_name() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field.toString());
  }

  @Test
  public void should_stub_equals_to_match_same_instance() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field.equals(test.field));
  }

  @Test
  public void should_stub_equals_to_not_match_not_same_instance() {
    class TestClass {
      Object field;
      Object otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertFalse(test.field.equals(test.otherField));
    assertFalse(test.otherField.equals(test.field));
  }

  @Test
  public void should_stub_equals_to_not_match_null() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertFalse(test.field.equals(null));
  }

  @Test
  public void should_stub_hashcode_to_obey_contract() {
    class TestClass {
      Object field, otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(test.field.hashCode(), test.field.hashCode());
    assertTrue(test.field.hashCode() != test.otherField.hashCode());
  }

  @Test
  public void should_skip_not_null() {
    class TestClass {
      Object field;
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
    Object object = new Object();
    test.field = object;
    givenTest(test);
    assertSame(object, test.field);
    assertEquals("value", test.stringField);
    assertEquals(Boolean.TRUE, test.booleanField);
    assertEquals(Character.valueOf((char) 1), test.characterField);
    assertEquals(Byte.valueOf((byte) 1), test.byteField);
    assertEquals(Short.valueOf((short) 1), test.shortField);
    assertEquals(Integer.valueOf(1), test.integerField);
    assertEquals(Long.valueOf(1), test.longField);
    assertEquals(Float.valueOf(1), test.floatField);
    assertEquals(Double.valueOf(1), test.doubleField);
  }

  @Test
  public void should_skip_primitive_equal_to_binary_zero() {
    class TestClass {
      boolean booleanField;
      char charField;
      byte byteField;
      short shortField;
      int intField;
      long longField;
      float floatField;
      double doubleField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.booleanField == false);
    assertTrue(test.charField == (char) 0);
    assertTrue(test.byteField == 0);
    assertTrue(test.shortField == 0);
    assertTrue(test.intField == 0);
    assertTrue(test.longField == 0);
    assertTrue(test.floatField == 0);
    assertTrue(test.doubleField == 0);
  }

  @Test
  public void should_skip_primitive_not_equal_to_binary_zero() {
    class TestClass {
      boolean booleanField = true;
      char charField = 'a';
      byte byteField = 1;
      short shortField = 1;
      int intField = 1;
      long longField = 1;
      float floatField = 1;
      double doubleField = 1;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.booleanField == true);
    assertTrue(test.charField == 'a');
    assertTrue(test.byteField == 1);
    assertTrue(test.shortField == 1);
    assertTrue(test.intField == 1);
    assertTrue(test.longField == 1);
    assertTrue(test.floatField == 1);
    assertTrue(test.doubleField == 1);
  }

  @Test
  public void should_fail_for_final_class() {
    final class FinalClass {}
    class TestClass {
      @SuppressWarnings("unused")
      FinalClass field;
    }
    TestClass test = new TestClass();
    try {
      givenTest(test);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_inject_string() {
    class TestClass {
      String field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field);
  }

  @Test
  public void should_inject_primitives() {
    class TestClass {
      boolean booleanField;
      char characterField;
      byte byteField;
      short shortField;
      int integerField;
      long longField;
      float floatField;
      double doubleField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(false, test.booleanField);
    assertEquals(0, test.characterField);
    assertEquals(0, test.byteField);
    assertEquals(0, test.shortField);
    assertEquals(0, test.integerField);
    assertEquals(0, test.longField);
    assertEquals(0, test.floatField, 0);
    assertEquals(0, test.doubleField, 0);
  }

  @Test
  public void should_inject_wrappers() {
    class TestClass {
      Void voidField;
      Boolean booleanField;
      Character characterField;
      Byte byteField;
      Short shortField;
      Integer integerField;
      Long longField;
      Float floatField;
      Double doubleField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(null, test.voidField);
    assertEquals(Boolean.FALSE, test.booleanField);
    assertEquals(Character.valueOf((char) 0), test.characterField);
    assertEquals(Byte.valueOf((byte) 0), test.byteField);
    assertEquals(Short.valueOf((short) 0), test.shortField);
    assertEquals(Integer.valueOf(0), test.integerField);
    assertEquals(Long.valueOf(0), test.longField);
    assertEquals(Float.valueOf(0), test.floatField);
    assertEquals(Double.valueOf(0), test.doubleField);
  }

  @Test
  public void should_inject_array_of_primitives() {
    class TestClass {
      boolean[] booleans;
      char[] characters;
      byte[] bytes;
      short[] shorts;
      int[] integers;
      long[] longs;
      float[] floats;
      double[] doubles;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(1, test.booleans.length);
    assertEquals((new boolean[1])[0], test.booleans[0]);
    assertArrayEquals(new char[1], test.characters);
    assertArrayEquals(new byte[1], test.bytes);
    assertArrayEquals(new short[1], test.shorts);
    assertArrayEquals(new int[1], test.integers);
    assertArrayEquals(new long[1], test.longs);
    assertArrayEquals(new float[1], test.floats, 0);
    assertArrayEquals(new double[1], test.doubles, 0);
  }

  @Test
  public void should_inject_array_of_wrappers() {
    class TestClass {
      Boolean[] booleans;
      Character[] characters;
      Byte[] bytes;
      Short[] shorts;
      Integer[] integers;
      Long[] longs;
      Float[] floats;
      Double[] doubles;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertArrayEquals(new Boolean[] { false }, test.booleans);
    assertArrayEquals(new Character[] { 0 }, test.characters);
    assertArrayEquals(new Byte[] { 0 }, test.bytes);
    assertArrayEquals(new Short[] { 0 }, test.shorts);
    assertArrayEquals(new Integer[] { 0 }, test.integers);
    assertArrayEquals(new Long[] { 0L }, test.longs);
    assertArrayEquals(new Float[] { 0f }, test.floats);
    assertArrayEquals(new Double[] { 0.0 }, test.doubles);
  }

  @Test
  public void should_inject_array_of_strings() {
    class TestClass {
      String[] strings;
      String[][] deepStrings;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertArrayEquals(new String[] { "strings[0]" }, test.strings);
    assertArrayEquals(new String[][] { new String[] { "deepStrings[0][0]" } }, test.deepStrings);
  }

  @Test
  public void should_inject_array_of_mocks() {
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
  public void should_inject_reflection_classes() {
    class TestClass {
      Class<?> clazz;
      Field field;
      Method method;
      Constructor<?> constructor;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertNotNull(test.clazz);
    assertNotNull(test.field);
    assertNotNull(test.method);
    assertNotNull(test.constructor);
    assertEquals(test.clazz, test.field.getDeclaringClass());
    assertEquals(test.clazz, test.method.getDeclaringClass());
    assertEquals(test.clazz, test.constructor.getDeclaringClass());
  }

  @Test
  public void should_inject_enum() {
    class TestClass {
      ElementType field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(ElementType.class, test.field.getClass());
  }

  public static interface Interface {}
}
