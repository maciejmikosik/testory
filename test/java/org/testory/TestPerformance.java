package org.testory;

import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPerformance {
  @Before
  @After
  public void purge_history() {
    when("");
    when("");
  }

  @Test(timeout = 2000)
  public void logs_many_invocations() {
    int numberOfInvocations = 100 * 1000;
    Object mock = mock(Object.class);
    for (int i = 0; i < numberOfInvocations; i++) {
      mock.toString();
    }
    thenCalledTimes(numberOfInvocations, mock).toString();
  }
}
