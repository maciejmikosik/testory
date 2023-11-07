package org.testory.proxy.proxer;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    List list = mock(List.class);
    given(willReturn(3f), list).size();
    when(list.size());
    thenReturned(3);
  }
}
