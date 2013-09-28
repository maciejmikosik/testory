package org.testory.mock;

import org.testory.common.Nullable;

public interface Handler {
  @Nullable
  Object handle(Invocation invocation) throws Throwable;
}
