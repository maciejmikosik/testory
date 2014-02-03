package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_verification {
  private Object object, otherObject;
  private Object matcher;
  private List<Object> mock, otherMock;
  private On on;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object number) {
        return false;
      }
    };
    mock = mock(List.class);
    otherMock = mock(List.class);
  }

  @Test
  public void asserts_invocation_without_arguments() {
    mock.size();
    thenCalled(mock).size();
  }

  @Test
  public void asserts_invocation_with_argument() {
    mock.add(object);
    thenCalled(mock).add(object);
  }

  @Test
  public void fails_for_different_instance() {
    otherMock.size();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + mock + ".size()\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_different_method() {
    mock.clear();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + mock + ".size()\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_different_argument() {
    mock.add(otherObject);
    try {
      thenCalled(mock).add(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + mock + ".add(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_missing_invocation() {
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + mock + ".size()\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_repeated_invocation() {
    mock.size();
    mock.size();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + mock + ".size()" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void asserts_invocation_with_custom_logic() {
    mock.add(object);
    thenCalled(new On() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("add")
            && invocation.arguments.equals(Arrays.asList(object));
      }
    });
  }

  @Test
  public void fails_invocation_with_custom_logic() {
    try {
      on = new On() {
        public boolean matches(Invocation invocation) {
          return invocation.instance == mock && invocation.method.getName().equals("add")
              && invocation.arguments.equals(Arrays.asList(object));
        }
      };
      thenCalled(on);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times 1\n" //
          + "    " + on + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void asserts_exact_number_of_invocations() {
    mock.size();
    mock.size();
    mock.size();

    thenCalledTimes(3, mock).size();
    thenCalledTimes(3, new On() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("size");
      }
    });
  }

  @Test
  public void asserts_zero_number_of_invocations() {
    thenCalledTimes(0, mock).size();
    thenCalledTimes(0, new On() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("size");
      }
    });
  }

  @Test
  public void fails_for_less_number_of_invocations() {
    mock.size();
    mock.size();

    try {
      thenCalledTimes(3, mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + 3 + "\n" //
          + "    " + mock + ".size()" + "\n" //
      , e.getMessage());
    }

    try {
      on = new On() {
        public boolean matches(Invocation invocation) {
          return invocation.instance == mock && invocation.method.getName().equals("size");
        }
      };
      thenCalledTimes(3, on);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + 3 + "\n" //
          + "    " + on + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_more_number_of_invocations() {
    mock.size();
    mock.size();
    mock.size();
    mock.size();

    try {
      thenCalledTimes(3, mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + 3 + "\n" //
          + "    " + mock + ".size()" + "\n" //
      , e.getMessage());
    }

    try {
      on = new On() {
        public boolean matches(Invocation invocation) {
          return invocation.instance == mock && invocation.method.getName().equals("size");
        }
      };
      thenCalledTimes(3, on);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + 3 + "\n" //
          + "    " + on + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void asserts_matching_number_of_invocations() {
    mock.size();
    mock.size();
    mock.size();

    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object number) {
        return number.equals(3);
      }
    };
    thenCalledTimes(matcher, mock).size();
    thenCalledTimes(matcher, new On() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock && invocation.method.getName().equals("size");
      }
    });
  }

  @Test
  public void fails_for_not_matching_number_of_invocations() {
    mock.size();
    mock.size();

    matcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object number) {
        return number.equals(3);
      }

      public String toString() {
        return "matcher";
      }
    };

    try {
      thenCalledTimes(matcher, mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + matcher + "\n" //
          + "    " + mock + ".size()" + "\n" //
      , e.getMessage());
    }

    try {
      on = new On() {
        public boolean matches(Invocation invocation) {
          return invocation.instance == mock && invocation.method.getName().equals("size");
        }
      };
      thenCalledTimes(matcher, on);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called times " + matcher + "\n" //
          + "    " + on + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void verification_does_not_affect_following_verifications() {
    mock.size();
    thenCalled(mock).size();
    thenCalled(mock).size();
  }

  @Test
  public void invocations_inside_when_are_included() {
    when(mock.size());
    thenCalled(mock).size();
  }

  @Test
  public void invocations_before_when_are_included() {
    mock.size();
    when("do something");
    thenCalled(mock).size();
  }

  @Test
  public void invocations_before_when_closure_are_included() {
    mock.size();
    when(new Closure() {
      public Object invoke() throws Throwable {
        return null;
      }
    });
    thenCalled(mock).size();
  }

  @Test
  public void number_of_calls_cannot_be_negative() {
    try {
      thenCalledTimes(-1, mock);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(-1, new On() {
        public boolean matches(Invocation invocation) {
          return false;
        }
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void number_matcher_cannot_be_any_object() {
    matcher = new Object();
    try {
      thenCalledTimes(matcher, mock);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(matcher, new On() {
        public boolean matches(Invocation invocation) {
          return false;
        }
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void number_matcher_cannot_be_null() {
    try {
      thenCalledTimes(null, mock);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(null, new On() {
        public boolean matches(Invocation invocation) {
          return false;
        }
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_null() {
    try {
      thenCalled((Object) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(1, (Object) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(matcher, (Object) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void on_cannot_be_null() {
    try {
      thenCalled((On) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(1, (On) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(matcher, (On) null);
      fail();
    } catch (TestoryException e) {}
  }
}
