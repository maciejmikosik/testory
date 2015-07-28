[overview](#overview) | [mocks](#mocks) | [utilities](#utilities) | [macros](#macros) | [fine points](#fine-points) | [troubleshooting](#troubleshooting) | [development](#development)

# Overview
[when](#when) | [thenReturned](#thenreturned) | [thenThrown](#thenthrown)

To make `given`, `when`, `then` family of methods available, add following import to your test class.

        import static org.testory.Testory.*;

The most basic purpose of `given` and `when` is decorative so instead of writing comments like this

        // given
        list = new ArrayList<String>();
        // when
        list.add("element");

you wrap lines inside methods

        given(list = new ArrayList<String>());
        when(list.add("element"));

The purpose of `then` is to make assertions similar to junit's assertions.

 - `then(boolean)` is like `assertTrue(boolean)`
 - `then(Object, Object)` is like `assertThat(T, Matcher<T>)`
 - `thenEqual(Object, Object)` is like `assertEquals(Object, Object)`

Example test using `given`-`when`-`then` looks like this

        given(list = new ArrayList<String>());
        when(list.add("element"));
        then(!list.isEmpty());

`given` and `when` can be used in chained form. This is helpful in various situations like dealing with `void` methods.

Because using `void` as an argument will not compile

 - use `given(list).clear()` instead of `given(list.clear())`
 - use `when(list).clear()` instead of `when(list.clear())` 

### when

Assertions can be used to verify result of invocation that happened at line containing `when`.

Result that is going to be asserted is

 - `Object` passed as an argument

        when(object);

 - `Object` returned, or `Throwable` thrown, by chained method

        when(instance).chainedMethod()

 - result of invoking `Closure`

        when(new Closure() {
          public Object invoke() throws Throwable {
            // custom logic
          }
        });

### thenReturned

`thenReturned` is used to make assertions about `Object` returned by `when`. Assertion fails if actual result is not equal to expected `Object` or value.

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.get(0));
        thenReturned("element");

[Matchers](#matchers) can be used to make custom assertions

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.clone());
        thenReturned(not(sameInstance(list)));

### thenThrown

`thenThrown` is used to make assertions about `Throwable` thrown by `when`. Because of
java syntax `when` must be in chained form.

        given(list = new ArrayList<String>());
        when(list).get(0);
        thenThrown(IndexOutOfBoundsException.class);

`thenThrown` is overloaded to accept `Throwable` instance, `Class` or matcher.

Notice that `when` in chained form catches any `Throwable`. This prevents `Throwable` from failing a test. If you want this to be detected, use either `thenReturned` or `thenThrown` to assert expected result.

# Mocks
[stubbing](#stubbing) | [verifying](#verifying) | [matching invocations](#matching-invocations) | [spying](#spying)

Any non-final class or interface can be mocked.

        given(list = mock(List.class));

Newly created mock has following properties
 - all methods are stubbable, except finalize and final methods
 - mock is nice, returning null or binary zero for unstubbed methods
 - mock is conveniently prestubbed
   - `toString` is stubbed to contain class name and unique ordinal
   - `equals` is stubbed so mock is equal only to itself
   - `hashCode` is stubbed to obey contract

### Stubbing

Mock can be stubbed to return `Object`

        given(willReturn(object), list).get(1);

throw `Throwable`

        given(willThrow(new IndexOutOfBoundsException()), list).get(2);

or execute custom logic.

        given(new Handler() {
          public Object handle(Invocation invocation) throws Throwable {
            // custom logic
          }
        }, mock).toString();

Stubbing will be only effective for specified instance of mock, method and equal arguments.

 - `void` method can be stubbed to "just return" using `willReturn(null)`
 - `willThrow` [fills in stack trace](http://docs.oracle.com/javase/7/docs/api/java/lang/Throwable.html#fillInStackTrace()) upon throwing, `willRethrow` does not
 - returning `Object` incompatible with method return type causes `TestoryException` upon invocation
 - throwing `Throwable` incompatible with method declaration causes `TestoryException` upon invocation

### Verifying

It is possible to assert expected invocation on mock.

        given(output = mock(OutputStream.class));
        given(filterOutput = new FilterOutputStream(output));
        when(filterOutput).close();
        thenCalled(output).close();

Invocation is expected to be called exactly once.

You can verify number of invocations by passing exact value (may be 0)

        thenCalledTimes(3, mock).size();

or using matcher.
 
        thenCalledTimes(greaterThan(0), mock).toString();

By default, order of invocations does not matter.

If you need to assert that invocations happened in order, use ordered verifying.

        thenCalledInOrder(mockDatabase).open();
        thenCalledInOrder(mockDatabase).close();

### Matching Invocations

You can take full control of matching invocations by implementing your own `InvocationMatcher`.

        InvocationMatcher onCondition = new InvocationMatcher() {
          public boolean matches(Invocation invocation) {
            // custom logic
          }
        };

Or use predefined factories for most common cases.

 - To assert that specific number of invocations was called on mock.

        thenCalledTimes(4, onInstance(mock));

 - To assert that no invocations was called on mock.

        thenCalledNever(onInstance(mock));

 - To stub all invocations returning specified type. See [komarro library](https://code.google.com/p/komarro/) for explanation why would you want to do that.

        given(willReturn(person), onReturn(Person.class));

 - To stub all invocations by return type and arguments.

        given(willReturn(person), onRequest(Person.class, "username"));

Use `any` if you do not care about argument value during stubbing or verification.

        thenCalled(list).add(any(Object.class));

or `any` with [matcher](#matchers) if you care

        thenCalled(list).add(any(Object.class, startsWith("prefix")));

`Class` passed to `any` is just for inferring purpose. Argument can be instance of any type and still can match.

In most cases you can mix `any` with real arguments.

        given(willReturn(true), mock).someMethod(object, any(Object.class));

In cases where you cannot (due to technical limitations) `TestoryException` is thrown.

        // throws TestoryException
        given(willReturn(true), mock).someMethod(any(int.class), intValue);

You can workaround those cases by wrapping primitive values in `a`.

        given(willReturn(true), mock).someMethod(any(int.class), a(intValue));

Use `the` shortcut if you expect exactly same instance

        given(willReturn(true), mock).someMethod(the(instance));

### Spying

Spy is a mock that is prestubbed to delegate all invocations to real object.

You can create spy as a new mock

        given(real = Arrays.asList("a", "b", "c"));
        given(spy = spy(real));

or stub an existing mock to act as a spy

        given(real = Arrays.asList("a", "b", "c"));
        given(mock = mock(List.class));
        given(willSpy(real), onInstance(mock));

Spies can be stubbed and verified like any other mock.

 - real object does not have to be actually real, it can be other mock/spy
 - there can be many spies for same real object

# Utilities
[matchers](#matchers) | [closures](#closures)

### Matchers

Wherever api method accepts `Object` via parameter named `matcher`, you are free to pass any compatible matcher

 - [org.hamcrest.Matcher](https://github.com/hamcrest/JavaHamcrest)
 - [org.fest.assertions.Condition](https://github.com/alexruiz/fest-assert-1.x)
 - [org.fest.assertions.core.Condition](https://github.com/alexruiz/fest-assert-2.x)
 - [org.assertj.core.api.Condition](https://github.com/joel-costigliola/assertj-core)
 - [com.google.common.base.Predicate](https://code.google.com/p/guava-libraries/)
 - [com.google.common.base.Function](https://code.google.com/p/guava-libraries/)
 - dynamic matcher
 
        Object matcher = new Object() {
          public boolean matches(Object item) {
            return ...;
          }
        };

### Closures

In some cases `when` can be difficult to write. For example you want to assert that
`Throwable` was thrown, but cannot use chained form of `when`, because method is static. You may then
wrap call inside `Closure`.

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

# Macros
[givenTimes](#giventimes) | [givenTry](#giventry) | [givenTest](#giventest)

Macros help you remove boilerplate code from your tests.

### givenTimes

Repeats invocation many times.

        given(list = new ArrayList<String>());
        givenTimes(5, list).add("element");
        when(list.size());
        thenReturned(5);

### givenTry

Catches possible `Throwable` thrown by chained method allowing test to run forward.

        given(list = new ArrayList<String>());
        givenTry(list).add(5, "element");
        when(list.size());
        thenReturned(0);

### givenTest (beta)

Initializes each field of `this` test and fails if initialization of any field fails.
Also purges testory internal state (see [purging](#purging)).

        @Before
        public void before() {
          givenTest(this);
        }

Field is ignored if

 - field is primitive already assigned to something other than binary zero
 - field is reference already assigned to something other than null
 - field is final
 - field is static
 - field is not declared in `this` test's class but in it's superclass

Initialization depends on type of field.

Field of array type is initialized to array of size 1. Array's cell is initialized recursively.

Field of non-final type is assigned to mock. Mock is conveniently prestubbed
 - `toString` is stubbed to return name of field
 - `equals` is stubbed so mock is equal only to itself
 - `hashCode` is stubbed to obey contract

Field of final type is assigned to sample data

 - `boolean`, `Boolean` is assigned to random value.
 - `char`, `Character` is assigned to random lowercase letter.
 - `byte`, `Byte`, `short`, `Short`, `int`, `Integer`, `long`, `Long` is assigned to random value such as
   - `value * value * value` does not cause overflow
   - is not equal to any of: -1, 0, 1
 - `float`, `Float`, `double`, `Double` is assigned to random value such as
   - `value * value * value` does not cause overflow or underflow
   - is not equal to +0.0 or -0.0
 - `String` is initialized to name of field.
 - `enum` is initialized to random constant.
 - `Class` is initialized to some sample class.
 - `Field`, `Method` and `Constructor` is assigned to member of sample class.

Random sample data is deterministically generated using field type and field name as a seed.

# Fine Points
[arrays](#arrays) | [primitives](#primitives) | [finals](#finals) | [purging](#purging)

### Arrays

In java, arrays are objects and invoking `equals` performs identity comparison.
For sake of convenience, testory treats arrays as values and performs deep equals.
This happens in

 - `thenReturned`
 - `thenEqual`
 - stubbing (comparing arguments)
 - verifying (comparing arguments)

Error messages also print contents of array where possible.

### Primitives

For convenience, testory does not differentiate between primitives and wrapper types.
Necessary boxing/unboxing is performed behind the scenes.
This applies to type of

 - object passed to `when`
 - object returned from chained `when`
 - object returned from `Closure`
 - object expected by `thenReturned`
 - objects compared by `thenEqual`
 - object matched by `then`
 - object returned from `Handler` (including object passed to `willReturn`)
 - object passed to `a`

### Finals

Due to technical limitations, testory does not play well with final classes and final methods.

Final classes cannot be mocked or chained. Any of the following throws `TestoryException`.

 - `mock(FinalClass.class)`
 - `when(instanceOfFinalClass).method()`
 - `givenTry(instanceOfFinalClass).method()`
 - `givenTimes(instanceOfFinalClass).method()`

Final methods on mock should not be invoked, stubbed or verified.
Final methods on any object should not be proxied (used in chained form).
Any of the following invokes real method on unreal (mocked/proxied) object causing undetermined effects!

 - `mock.finalMethod()`
 - `given(willReturn(object), mock).finalMethod()`
 - `thenCalled(mock).finalMethod()`
 - `when(instance).finalMethod()`
 - `givenTry(instance).finalMethod()`
 - `givenTimes(n, instance).finalMethod()`

### Purging (beta)

Testory maintains global state that holds information about every mock, stubbing and invocation. This data needs to be periodically released to prevent running out of memory. Since testory has no foolproof way to tell whether one test ended and another started, it relies on some simplistic assumptions

 - Only one `when` is used per one test. Thus calling `when`, makes testory to forget about all events that happened before previous `when`.
 - Initialization using `givenTest` is done only once at the very beginning of each test. This makes testory to forget about all events that happened before.

Purging has following consequences

 - calling any method on purged mock throws `TestoryException`
 - purged stubbing is no longer in effect
 - purged invocation is not included during verification
 - purged mock is no longer considered to be a mock, so
  - calling it causes `TestoryException`
  - stubbing it causes `TestoryException`
  - verifying it causes `TestoryException`

# Troubleshooting

### `NoClassDefFoundError`, package *** does not exist

Testory uses `Thread.currentThread().getContextClassLoader()` to load classes generated for mocks. If you use your own classloader, it is your responsibility to properly set context classloader using `Thread.currentThread().setContextClassLoader()`. If you skip that, context classloader by default points out to system classloader (`ClassLoader.getSystemClassLoader()`).

Solution is to remember to set context classloader to be same as or child of classloader that loaded testory library.

# Development
[building](#building) | [contributing](#contributing)

### Building

You can build `testory.jar` by running bash script `./run/build` from project directory.
Since 1.0.0 build is deterministic. This means you get identical `testory.jar` file every time.
If you build version that has a release tag (like `v1.0.0`), it should be identical to released file.

### Contributing

If you found a bug, have an idea for a new feature, or just a question, please post it as github issue.

If you want to contribute a code, please do not use github's pull request feature.
Just publish changes anywhere and post a link to your repository in relevant issue.
Simplest way is to fork testory repository on github.
