package jdk.java.lang.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;

public class learn_Method {
  private Method method, otherMethod;

  @Test
  public void overriding_method_is_not_equal_to_overridden() throws NoSuchMethodException {
    class Original {
      @SuppressWarnings("unused")
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
      @SuppressWarnings("unused")
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
      @SuppressWarnings("unused")
      void method() {}
    }
    class Mirror {
      @SuppressWarnings("unused")
      void method() {}
    }
    method = AClass.class.getDeclaredMethod("method");
    try {
      method.invoke(new Mirror());
      fail();
    } catch (IllegalArgumentException e) {}
  }
}
