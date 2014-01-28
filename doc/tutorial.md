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

Mocks
=====

Any non-final class or interface can be mocked.

        given(list = mock(List.class));

Mock can be stubbed to return Object or throw Throwable

        given(willReturn(object), list).get(1);
        given(willThrow(new IndexOutOfBoundsException()), list).get(2);

or stubbed with custom logic.

        given(new Will() {
          public Object handle(Invocation invocation) throws Throwable {
            // custom logic
          }
        }, mock).toString();

Stubbing will be only effective for specified instance of mock, method and equal arguments.

Newly created mock has following properties
 - all methods are stubbable, except finalize and final methods
 - mock is nice, returning null or binary zero for unstubbed methods
 - mock is conveniently prestubbed
   - toString is stubbed to contain class name and identity hash code
   - equals is stubbed so mock is equal only to itself
   - hashCode is stubbed to identity hash code

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

Initialization of test fields
=============================

`givenTest` initializes each field of **this** test and fails if initialization of any field fails.

        @Before
        public void before() {
          givenTest(this);
        }

Field is ignored if

 - field is primitive already assigned to something other than binary zero
 - field is reference already assigned to something other than null
 - field is final
 - field is static
 - field is not declared in **test**'s class but in it's superclass

Initialization depends on type of field.

Array
-----

Field of array type is initialized to array of size 1. Array's cell is initialized recursively.

Final type
----------

Field of final type is assigned to sample data

 - `boolean`, `Boolean` is assigned to random value.
 - `char`, `Character` is assigned to random lowercase letter.
 - `byte`, `Byte`, `short`, `Short`, `int`, `Integer`, `long`, `Long` is assigned to random value such as
   - `value * value * value` does not cause overflow
   - is not equal to any of: -1, 0, 1
 - `float`, `Float`, `double`, `Double` is assigned to random value such as
   - `value * value * value` does not cause overflow or underflow
   - is not equal to +0.0 or -0.0
 - String is initialized to name of field.
 - Enum is initialized to random constant.
 - Class is initialized to some sample class.
 - Field, Method and Constructor is assigned to member of sample class.

Random sample data is deterministically generated using field type and field name as a seed.

Non-final type
--------------

Field of non-final type is assigned to mock.
Mock is conveniently prestubbed
   - toString is stubbed to return name of field
   - equals is stubbed so mock is equal only to itself
   - hashCode is stubbed to obey contract
