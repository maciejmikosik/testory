package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.canAssign;
import static org.testory.common.Classes.canReturn;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class test_Classes_canReturn {
  private List<Object> objects;
  private List<Method> methods;

  @Before
  public void before() {
    objects = new ArrayList<Object>();
    methods = new ArrayList<Method>();
  }

  @Test
  public void can_return_object_which_is_assignable_to_method_return_type() {
    abstract class Methods {
      // @formatter:off
      abstract void returnPrimitiveVoid();
      abstract boolean returnPrimitiveBoolean();
      abstract char returnPrimitiveChar();
      abstract byte returnPrimitiveByte();
      abstract short returnPrimitiveShort();
      abstract int returnPrimitiveInt();
      abstract long returnPrimitiveLong();
      abstract float returnPrimitiveFloat();
      abstract double returnPrimitiveDouble();
      abstract Void returnWrapperVoid();
      abstract Boolean returnWrapperBoolean();
      abstract Character returnWrapperChar();
      abstract Byte returnWrapperByte();
      abstract Short returnWrapperShort();
      abstract Integer returnWrapperInt();
      abstract Long returnWrapperLong();
      abstract Float returnWrapperFloat();
      abstract Double returnWrapperDouble();

      abstract Object returnObject();
      abstract String returnString();

      abstract List<Object> returnList();
      abstract ArrayList<Object> returnArrayList();
      abstract LinkedList<Object> returnLinkedList();
      // @formatter:on
    }

    for (Method method : Methods.class.getDeclaredMethods()) {
      if (method.getName().startsWith("return")) {
        methods.add(method);
      }
    }
    assume(methods.size() > 10);

    objects.addAll(asList(false, 'a', (byte) 0, (short) 0, 0, 0L, 0f, 0.0));
    objects.addAll(asList(new Object(), "string", null));
    objects.addAll(asList(Arrays.asList(), new ArrayList<Object>(), new LinkedList<Object>()));

    for (Method method : methods) {
      for (Object object : objects) {
        boolean canAssign = canAssign(object, method.getReturnType());
        boolean canReturn = canReturn(object, method);
        assertEquals(formatMessage(method, object), canAssign, canReturn);
      }
    }
  }

  @Test
  public void method_cannot_be_null() {
    try {
      canReturn(new Object(), null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static void assume(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }

  private static String formatMessage(Method method, Object object) {
    return "can return " + object + " from " + method.getName();
  }
}
