package org.testory.java;

import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class learn_ClassLoader {
  @Test
  public void jdk_classes_are_loaded_by_bootstrap_class_loader() {
    assertNull(Object.class.getClassLoader());
    assertNull(String.class.getClassLoader());
    assertNull(List.class.getClassLoader());
  }
}
