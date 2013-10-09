Overview
========

To make **given**, **when**, **then** family of methods available, add following import to your test class.

        import static org.testory.Testory.*;

The most basic purpose of **given** and **when** is decorative so instead of writing comments like this

        // given
        list = new ArrayList<String>();
        // when
        list.add("element");

you wrap lines inside methods

        given(list = new ArrayList<String>());
        when(list.add("element"));

The purpose of **then** is to make an assertion. The most basic assertion asserts that condition is true. This works just like junit's **assertTrue**.

        given(list = new ArrayList<String>());
        when(list.add("element"));
        then(!list.isEmpty());

**Given** and **when** can be used in chained form. This is helpful in various situations like dealing with void methods. Because void cannot be an argument

        // does not compile
        given(list.clear());
        when(list.clear());

you should use chained forms.

        given(list).clear();
        when(list).clear();


Assertions
==========

Standalone
----------

Some assertions can be used on their own. They are similar to junit's assertions

 - `then(boolean)` is like `assertTrue(boolean)`
 - `then(Object, Object)` is like `assertThat(T, Matcher<T>)`
 - `thenEqual(Object, Object)` is like `assertEquals(Object, Object)`

Non-standalone
--------------

Non-standalone assertions are used to verify result of invocation happened at line with **when**.
Result is object passed to **when** as an argument.

        when(object);
        
If **when** is in chained form, result is object returned, or throwable thrown, by chained method.

        when(instance).chainedMethod()  

Non-standalone assertions are **thenReturned** and **thenThrown**.

thenReturned
------------

**thenReturned** is used to make assertions about result of **when**. Assertion fails if result is not equal to expected.

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.get(0));
        thenReturned("element");

Matchers can be used to make custom assertions

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.clone());
        thenReturned(not(sameInstance(list)));

thenThrown
----------

**thenThrown** is used to make assertions about throwable thrown by **when**. Because of
java syntax **when** must be in chained form.

        given(list = new ArrayList<String>());
        when(list).get(0);
        thenThrown(IndexOutOfBoundsException.class);

Notice that **when** in chained form catches any throwable. This prevents throwable from failing a test if result of **when** is not asserted by non-standalone assertion.
**thenThrown** is overloaded to accept throwable instance, class or matcher.

Utilities
=========

Matchers
--------

Wherever api method accepts Object, but states that it accepts matcher, you are free to pass any of compatible matchers

 - org.hamcrest.Matcher
 - org.fest.assertions.Condition
 - com.google.common.base.Predicate
 - com.google.common.base.Function
 - dynamic matcher
 
        Object matcher = new Object() {
          public boolean matches(Object item) {
            return ...;
          }
        };

Closures
--------

In some cases **when** can be difficult to write. For example you want to assert that
throwable was thrown, but cannot use chained form of **when**, because method is static. You may then
wrap call inside Closure.

        @Test
        public void should_fail_if_malformed() {
          when(_parseInt("12x3"));
          thenThrown(NumberFormatException.class);
        }
        
        private static Closure _parseInt(final String string) {
          return new Closure() {
            public Integer invoke() {
              return Integer.parseInt(string);
            }
          };
        }


Macros
======

Macros help you remove boilerplate code from your tests.

givenTimes
----------

        given(list = new ArrayList<String>());
        givenTimes(5, list).add("element");
        when(list.size());
        thenReturned(5);

givenTry
--------

Catches possible throwable thrown by chained method allowing test to run forward. 

        given(list = new ArrayList<String>());
        givenTry(list).add(5, "element");
        when(list.size());
        thenReturned(0);

givenTest
---------

Initializes all test's fields at once.

        @Before
        public void before() {
          givenTest(this);
        }

This injects dummy (unstubbable mock) into each field of **test**.

Injected dummy has following properties

 - Object.toString() is stubbed to return name of declared field
 - Object.equals(Object) is stubbed so dummy is equal only to itself
 - Object.hashCode() is stubbed to obey contract

Field is skipped (nothing is injected) if

 - field is not null
 - field is of primitive type
 - field is not declared in **test**'s class but in it's superclass

If field is of final class - injection fails unless class is one of

 - array - array with single dummy element (recursively)
 - String - string equal to field's name
 - primitive wrapper (for example Integer) - valueOf(0) (Integer.valueOf(0))
 - enum - one of constants from enum declaration
 - Class - some concrete class
 - Method - some method declared in dummy class
 - Constructor - some constructor declared in dummy class
 - Field - some field declared in dummy class
