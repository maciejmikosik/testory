package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.spy;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willSpy;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_spying {
  private List<Object> mock, spy, real, otherMock;
  private Object a, b, c, x;

  @Before
  public void before() {
    mock = mock(List.class);
    otherMock = mock(List.class);
    a = "a";
    b = "b";
    c = "c";
    x = "x";
  }

  @Test
  public void invokes_method_on_real_object() {
    real = Arrays.asList(a, b, c);

    given(willSpy(real), onInstance(mock));
    assertEquals(b, mock.get(1));

    spy = spy(real);
    assertEquals(b, spy.get(1));
  }

  @Test
  public void throws_from_real_object_method() {
    real = Arrays.asList(a, b, c);

    given(willSpy(real), onInstance(mock));
    try {
      mock.get(3);
      fail();
    } catch (IndexOutOfBoundsException e) {}

    spy = spy(real);
    try {
      spy.get(3);
      fail();
    } catch (IndexOutOfBoundsException e) {}
  }

  @Test
  public void can_be_stubbed() {
    real = Arrays.asList(a, b, c);

    given(willSpy(real), onInstance(mock));
    given(willReturn(x), mock).get(1);
    assertSame(a, mock.get(0));
    assertSame(x, mock.get(1));

    spy = spy(real);
    given(willReturn(x), spy).get(1);
    assertSame(a, spy.get(0));
    assertSame(x, spy.get(1));
  }

  @Test
  public void can_be_verified() {
    real = Arrays.asList(a, b, c);

    given(willSpy(real), onInstance(mock));
    mock.get(1);
    thenCalled(mock).get(1);
    try {
      thenCalled(mock).get(2);
      fail();
    } catch (TestoryAssertionError e) {}

    spy = spy(real);
    spy.get(1);
    thenCalled(spy).get(1);
    try {
      thenCalled(spy).get(2);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void can_spy_another_mock() {
    given(willSpy(otherMock), onInstance(mock));
    given(willReturn(x), otherMock).get(1);
    assertEquals(x, mock.get(1));

    spy = spy(otherMock);
    given(willReturn(x), otherMock).get(1);
    assertEquals(x, spy.get(1));
  }

  @Test
  public void cannot_spy_null() {
    try {
      willSpy(null);
      fail();
    } catch (TestoryException e) {}

    try {
      spy(null);
      fail();
    } catch (TestoryException e) {}
  }
}
