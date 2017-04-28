package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.proxy.Typing.typing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestTyping {
  private Class<?> superclass;
  private Set<? extends Class<?>> interfaces;

  @Before
  public void before() {
    superclass = Object.class;
    interfaces = classes(InterfaceA.class, InterfaceB.class);
  }

  @Test
  public void creates_legal_typing() {
    assertFactory(ConcreteClass.class, interfaces);
    assertFactory(AbstractClass.class, interfaces);
    assertFactory(ConcreteClass.class, classes());
    assertFactory(Object.class, interfaces);
  }

  private static void assertFactory(Class<?> superclass, Set<? extends Class<?>> interfaces) {
    Typing typing = typing(superclass, interfaces);
    assertEquals(superclass, typing.superclass);
    assertEquals(interfaces, typing.interfaces);
  }

  @Test
  public void fails_for_illegal_typing() {
    assertFactoryFails(Interface.class, interfaces);
    assertFactoryFails(AnnotationClass.class, interfaces);
    assertFactoryFails(int.class, interfaces);
    assertFactoryFails(Object[].class, interfaces);
    assertFactoryFails(superclass, classes(ConcreteClass.class));
    assertFactoryFails(superclass, classes(AbstractClass.class));
    assertFactoryFails(superclass, classes(AnnotationClass.class));
    assertFactoryFails(superclass, classes(int.class));
    assertFactoryFails(superclass, classes(Object[].class));
    assertFactoryFails(null, interfaces);
    assertFactoryFails(superclass, null);
  }

  private static void assertFactoryFails(Class<?> superclass, Set<? extends Class<?>> interfaces) {
    try {
      typing(superclass, interfaces);
      fail();
    } catch (ProxyException e) {}
  }

  private static Set<Class<?>> classes(Class<?>... classes) {
    return new HashSet<>(Arrays.asList(classes));
  }

  private static class ConcreteClass {}

  private static abstract class AbstractClass {}

  private static interface Interface {}

  private static interface InterfaceA {}

  private static interface InterfaceB {}

  private static @interface AnnotationClass {}
}
