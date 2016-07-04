package org.testory.plumbing.history;

import org.testory.common.Chain;

public interface History {
  Chain<Object> get();

  void set(Chain<Object> events);

  History add(Object event);
}
