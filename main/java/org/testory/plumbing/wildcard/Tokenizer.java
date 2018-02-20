package org.testory.plumbing.wildcard;

import static org.testory.common.Classes.tryWrap;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.proxy.Typing.subclassing;
import static org.testory.proxy.handler.ReturningHandler.returning;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import org.objenesis.ObjenesisStd;
import org.testory.proxy.Proxer;

public class Tokenizer {
  private final Proxer proxer;

  private Tokenizer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Tokenizer tokenizer(Proxer proxer) {
    check(proxer != null);
    return new Tokenizer(proxer);
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
      return (T) proxer.proxy(subclassing(type), returning(null));
    }
  }
}
