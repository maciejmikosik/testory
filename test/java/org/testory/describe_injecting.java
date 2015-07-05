package org.testory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Testilities.readDeclaredFields;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class describe_injecting {
  private final String string = "string";

  @Test
  public void injects_concrete_class() {
    class ConcreteClass {}
    class TestClass {
      ConcreteClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof ConcreteClass);
  }

  @Test
  public void injects_interface() {
    class TestClass {
      Interface field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Interface);
  }

  @Test
  public void injects_abstract_class() {
    abstract class AbstractClass {}
    class TestClass {
      AbstractClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof AbstractClass);
  }

  @Test
  public void injects_object_class() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Object);
  }

  @Test
  public void mock_is_nice() {
    class Foo {
      Object getObject() {
        throw new RuntimeException();
      }

      int getInt() {
        throw new RuntimeException();
      }

      void getVoid() {
        throw new RuntimeException();
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.getObject());
    assertEquals(0, foo.getInt());
    foo.getVoid();
  }

  @Test
  public void to_string_is_prestubbed() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field.toString());
  }

  @Test
  public void equals_is_prestubbed() {
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
  public void hashcode_is_prestubbed() {
    class TestClass {
      Object field, otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(test.field.hashCode(), test.field.hashCode());
    assertTrue(test.field.hashCode() != test.otherField.hashCode());
  }

  @Test
  public void injected_mock_is_stubbable() {
    class TestClass {
      List<Object> field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    given(willReturn(string), onInstance(test.field));
    assertEquals(string, test.field.get(0));
  }

  @Test
  public void injected_mock_is_restubbable() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    given(willReturn(string), onInstance(test.field));
    assertEquals(string, test.field.toString());
  }

  @Test
  public void injects_boolean() {
    @SuppressWarnings("unused")
    class TestClass {
      boolean p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Boolean w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    List<Object> fields = readDeclaredFields(test);
    assertTrue(fields.contains(false));
    assertTrue(fields.contains(true));
  }

  @Test
  public void injects_character() {
    @SuppressWarnings("unused")
    class TestClass {
      char p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Character w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Character character = (Character) object;
      assertTrue('a' <= character && character <= 'z');
    }
  }

  @Test
  public void injects_byte() {
    @SuppressWarnings("unused")
    class TestClass {
      byte p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Byte w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Byte number = (Byte) object;
      assertTrue("" + number, 2 <= Math.abs(number) && Math.abs(number) <= 5);
    }
  }

  @Test
  public void injects_short() {
    @SuppressWarnings("unused")
    class TestClass {
      short p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Short w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Short number = (Short) object;
      assertTrue("" + number, 2 <= Math.abs(number) && Math.abs(number) <= 31);
    }
  }

  @Test
  public void injects_integer() {
    @SuppressWarnings("unused")
    class TestClass {
      int p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Integer w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Integer number = (Integer) object;
      assertTrue("" + number, 2 <= Math.abs(number) && Math.abs(number) <= 1290);
    }
  }

  @Test
  public void injects_long() {
    @SuppressWarnings("unused")
    class TestClass {
      long p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Long w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Long number = (Long) object;
      assertTrue("" + number, 2 <= Math.abs(number) && Math.abs(number) <= 2097152);
    }
  }

  @Test
  public void injects_float() {
    @SuppressWarnings("unused")
    class TestClass {
      float p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Float w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Float number = (Float) object;
      assertTrue("" + number,
          Math.pow(2, -30) <= Math.abs(number) && Math.abs(number) <= Math.pow(2, 30));
    }
  }

  @Test
  public void injects_double() {
    @SuppressWarnings("unused")
    class TestClass {
      double p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
      Double w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
    }
    TestClass test = new TestClass();
    givenTest(test);

    for (Object object : readDeclaredFields(test)) {
      Double number = (Double) object;
      assertTrue("" + number,
          Math.pow(2, -300) <= Math.abs(number) && Math.abs(number) <= Math.pow(2, 300));
    }
  }

  @Test
  public void injects_string() {
    class TestClass {
      String field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field);
  }

  @Test
  public void injects_array_of_primitives() {
    class TestClass {
      int[] ints;
      Integer[] intWrappers;
      float[] floats;
      Float[] floatWrappers;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertNotEquals(0, test.ints[0]);
    assertNotNull(test.intWrappers[0]);
    assertNotEquals(0, test.floats[0]);
    assertNotNull(test.floatWrappers[0]);
  }

  @Test
  public void injects_array_of_strings() {
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
  public void injects_reflection_classes() {
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
  public void injects_enum() {
    @SuppressWarnings("unused")
    class TestClass {
      ElementType a, b, c, d, e, f, g, h, i, j;
    }
    TestClass test = new TestClass();
    givenTest(test);

    List<Object> fields = readDeclaredFields(test);
    assertTrue("" + fields, new HashSet<Object>(fields).size() > ElementType.values().length / 2);
  }

  @Test
  public void skips_not_null() {
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
    givenTest(test);
    assertEquals(true, test.booleanPrimitive);
    assertEquals(true, test.booleanWrapper);
    assertEquals('a', test.charField);
    assertEquals(1, test.byteField);
    assertEquals(1, test.shortField);
    assertEquals(1, test.intField);
    assertEquals(1, test.longField);
    assertEquals(1, test.floatField, 0);
    assertEquals(1, test.doubleField, 0);
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
      assertTrue(e.getMessage(), e.getMessage().contains("fieldOfFinalClass"));
    }
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
