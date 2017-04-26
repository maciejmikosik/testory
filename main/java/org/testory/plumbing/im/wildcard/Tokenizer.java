package org.testory.plumbing.im.wildcard;

import static org.testory.common.Classes.tryWrap;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.List;

import org.objenesis.ObjenesisStd;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.NoOp;

public class Tokenizer {
  private Tokenizer() {}

  public static Tokenizer tokenizer() {
    return new Tokenizer();
  }

  public <T> T token(Class<T> type) {
    check(type != null);
    if (type.isArray()) {
      return (T) Array.newInstance(type.getComponentType(), 0);
    } else if (type.isPrimitive()) {
      return (T) new ObjenesisStd().newInstance(tryWrap(type));
    } else if (!Modifier.isAbstract(type.getModifiers())) {
      return new ObjenesisStd().newInstance(type);
    } else {
      return newCglibProxy(type);
    }
  }

  private static <T> T newCglibProxy(Class<T> type) {
    Enhancer enhancer = new Enhancer() {
      /** includes all constructors */
      protected void filterConstructors(Class sc, List constructors) {}
    };
    enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());

    class CglibBugWorkaround {}
    if (type == Object.class) {
      enhancer.setSuperclass(CglibBugWorkaround.class);
      enhancer.setInterfaces(new Class[0]);
    } else if (type.isInterface()) {
      enhancer.setSuperclass(CglibBugWorkaround.class);
      enhancer.setInterfaces(new Class[] { type });
    } else {
      enhancer.setSuperclass(type);
      enhancer.setInterfaces(new Class[0]);
    }

    enhancer.setCallbackTypes(new Class[] { NoOp.class });

    Class<?> proxyClass;
    try {
      proxyClass = enhancer.createClass();
    } catch (CodeGenerationException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e);
    }

    Factory proxy = (Factory) new ObjenesisStd().newInstance(proxyClass);
    proxy.setCallback(0, NoOp.INSTANCE);
    return (T) proxy;
  }
}
