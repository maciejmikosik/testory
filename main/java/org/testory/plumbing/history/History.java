package org.testory.plumbing.history;

import org.testory.common.Chain;

public interface History {
  Chain<Object> get();

  History add(Object event);
}
