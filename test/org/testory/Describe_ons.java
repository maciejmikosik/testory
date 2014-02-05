package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.onReturn;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_ons {
  private Service mock, otherMock;
  private Data data;

  public interface Data {}

  public interface OtherData {}

  public interface Service {
    Data getData();

    OtherData getOtherData();
  }

  @Before
  public void before() {
    mock = mock(Service.class);
    otherMock = mock(Service.class);
    data = mock(Data.class);
  }

  @Before
  @After
  public void purge_to_isolate_tests() {
    when("");
    when("");
  }

  @Test
  public void matches_same_instance() {
    given(willReturn(data), onInstance(mock));
    assertSame(data, mock.getData());
    thenCalled(onInstance(mock));
  }

  @Test
  public void not_matches_other_instance() {
    given(willReturn(data), onInstance(mock));
    given(willReturn("mock"), mock).toString(); // makes mock printable in error message
    assertNotSame(data, otherMock.getData());
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    onInstance(%s)\n", //
          mock), //
          e.getMessage());
    }
  }

  @Test
  public void matches_return_type() {
    given(willReturn(data), onReturn(Data.class));
    assertSame(data, mock.getData());
    thenCalled(onReturn(Data.class));
  }

  @Test
  public void not_matches_other_return_type() {
    given(willReturn(data), onReturn(Data.class));
    assertNotSame(data, mock.getOtherData());
    try {
      thenCalled(onReturn(Data.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    onReturn(%s)\n", //
          Data.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void instance_cannot_be_null() {
    try {
      onInstance(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void return_type_cannot_be_null() {
    try {
      onReturn(null);
      fail();
    } catch (TestoryException e) {}
  }
}
