package jdk.java.lang.reflect;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Method;

import org.junit.Test;

@SuppressWarnings("unused")
public class learn_Method {
  private Method method, otherMethod;

  @Test
  public void overriding_method_is_not_equal_to_overridden() throws NoSuchMethodException {
    class Original {
      void method() {}
    }
    class Subclass extends Original {
      void method() {}
    }
    method = Original.class.getDeclaredMethod("method");
    otherMethod = Subclass.class.getDeclaredMethod("method");
    assertNotEquals(method, otherMethod);
  }

  @Test
  public void reflective_invocation_is_polymorphic() throws Exception {
    class Original {
      String method() {
        return "original";
      }
    }
    class Subclass extends Original {
      String method() {
        return "subclass";
      }
    }
    method = Original.class.getDeclaredMethod("method");
    assertEquals("subclass", method.invoke(new Subclass()));
  }

  @Test
  public void reflective_invocation_checks_instance_assignability() throws Exception {
    class AClass {
      void method() {}
    }
    class Mirror {
      void method() {}
    }
    method = AClass.class.getDeclaredMethod("method");
    try {
      method.invoke(new Mirror());
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void parameter_annotations_include_annotations_on_parameters()
      throws ReflectiveOperationException {
    class TestClass {
      void method(@TestAnnotation Object parameter) {}
    }
    method = TestClass.class.getDeclaredMethod("method", Object.class);
    Annotation[][] annotations = method.getParameterAnnotations();
    assertEquals(1, annotations.length);
    assertEquals(1, annotations[0].length);
    assertTrue(annotations[0][0] instanceof TestAnnotation);
  }

  @Test
  public void method_annotations_include_annotations_on_return_type()
      throws ReflectiveOperationException {
    class TestClass {
      @TestAnnotation
      void method(Object parameter) {}
    }
    method = TestClass.class.getDeclaredMethod("method", Object.class);
    Annotation[] annotations = method.getAnnotations();
    assertEquals(1, annotations.length);
    assertTrue(annotations[0] instanceof TestAnnotation);
  }

  @Retention(RUNTIME)
  @interface TestAnnotation {}
}
