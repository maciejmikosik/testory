package org.testory;

import static org.junit.Assert.assertSame;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_comparing_arrays {
  private List<?> mock;
  private int index;
  private Object object;

  @Before
  public void before() {
    mock = mock(List.class);
    index = 123;
    object = newObject("object");
  }

  @Test
  public void then_equals_uses_deep_equals() {
    thenEqual(new Object[] { new Object[] { object } }, new Object[] { new Object[] { object } });
  }

  @Test
  public void then_returned_uses_deep_equals() {
    when(new Object[] { new Object[] { object } });
    thenReturned(new Object[] { new Object[] { object } });
  }

  @Test
  public void stubbing_uses_deep_equals() {
    given(willReturn(index), mock).indexOf(new Object[] { new Object[] { object } });
    assertSame(index, mock.indexOf(new Object[] { new Object[] { object } }));
  }

  @Test
  public void verification_uses_deep_equals() {
    mock.indexOf(new Object[] { new Object[] { object } });
    thenCalled(mock).indexOf(new Object[] { new Object[] { object } });
  }
}
