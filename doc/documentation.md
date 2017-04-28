[overview](#overview) | [mocks](#mocks) | [utilities](#utilities) | [macros](#macros) | [fine points](#fine-points) | [development](#development)

# Overview
[then](#then) | [thenReturned](#thenreturned) | [thenThrown](#thenthrown) | [when](#when)

Traditionally, test written in [BDD](https://en.wikipedia.org/wiki/Behavior-driven_development) fashion has 3 sections: given, when, then.
Those sections are preceded by comments. Example test could look like this.

    public class ArrayListTest {
      private List<Object> list;

      @Test
      public void is_not_empty_after_adding_first_element() {
        // given
        list = new ArrayList<>();

        // when
        list.add("element");

        // then
        assertFalse(list.isEmpty());
      } 
    }

Testory makes your tests more human-readable.
It provides family of `given`, `when`, `then` methods that make those sections explicit.

    import static org.testory.Testory.*;

    public class ArrayListTest {
      private List<Object> list;

      @Test
      public void is_not_empty_after_adding_first_element() {
        given(list = new ArrayList<>());
        when(list.add("element"));
        then(!list.isEmpty());
      }
    }

This family has plenty of overloaded methods to cover scenarios like
asserting exceptions, accepting matchers, stubbing and verifying mocks and more.

### then

`then` methods allow you to assert that specific condition is true.
These are similar to junit assertion methods.

 - `then(boolean)` is like `assertTrue(boolean)`
 - `then(Object, Object)` is like `assertThat(T, Matcher<T>)`
 - `thenEqual(Object, Object)` is like `assertEquals(Object, Object)`

Those are standalone assertions, which means they do not rely on what happened at `when` line like other assertions do.

### thenReturned

Expression nested in `when` method usually returns a result.
You can assert your expectation about this result using `thenReturned` method.
Assertion fails if actual result is not equal to expected `Object` or value.

    given(list = asList("element"));
    when(list.get(0));
    thenReturned("element");

If you need more complicated logic than `equals` you can use [Matchers](#matchers) from external libraries.

    given(list = asList("element"));
    when(list.clone());
    thenReturned(not(sameInstance(list)));

### thenThrown

Expression nested in `when` can also throw `Throwable`.
Normally it would make a test fail, but sometimes `Throwable` is exactly what you expect.
Classic idiom for asserting that throwable was thrown uses `try-catch` clause.

    list = asList();
    try {
      list.get(0);
      fail();
    } catch (IndexOutOfBoundsException e) {}

Testory can catch `Throwable` for you, but you need to help by wrapping tested expression in lambda.
After that you can assert that caught `Throwable` meets you expectations.

    given(list = asList());
    when(() -> list.get(0));
    thenThrown(IndexOutOfBoundsException.class);

`thenThrown` is overloaded to accept `Throwable` instance, `Class` or matcher.
Of course assertion fails if expression did not throw anything.

### when

Asserting result of `void` method also requires help, because you cannot nest `void` expression as an argument.
You need to wrap an expression in lambda, even if you don't need to catch throwable.
And because now throwable is caught, it will not make test fail as usual.
To restore this behavior, you need to assert explicitly that you expect expression to not throw anything, using `thenReturned()`.

    given(list = new ArrayList<>());
    given(list.add("element"));
    when(() -> list.clear());
    thenReturned();
    then(list.isEmpty());

There is an alternative to using lambdas.
It involves invoking method on proxy returned by `when`.

    given(list = asList());
    when(list).get(0);
    thenThrown(IndexOutOfBoundsException.class);

    given(list = new ArrayList<String>());
    given(list.add("element"));
    when(list).clear();
    thenReturned();
    then(list.isEmpty());

If you can't use lambdas (you do not use java8), this looks like better alternative than using expanded anonymous classes.
However there is a downside, because not all types are proxiable (for example final classes).
In that case, `when` returns `null` and you get `NullPointerException`.
Also final methods are not proxied, which results in unpredictable behavior.
Additionally static calls cannot be written this way, because there is no `this` to wrap in proxy.

# Mocks
[stubbing](#stubbing) | [verifying](#verifying) | [matching invocations](#matching-invocations) | [spying](#spying)

Testory is a full-featured mocking framework with intuitive grammar.
Mocks are programmable objects inheriting interface from specific type.
You create a mock by providing `class` or `interface`.

    given(list = mock(List.class));

### Stubbing

Stubbing is telling a mock what should happen if particular invocation occurs.
You need to define what invocation you are talking about (what mock, which method with what arguments).
Also you need to define what should happen on invocation (return object, throw throwable).

One way to specify invocation is by providing mock, method and arguments explicitly.
More complex ways are described in [matching invocations section](#matching-invocations).
Thing that can happen on invocation can be as simple as returning an object.
For example, if you want `list.get(1)` to return `object`, you stub it using `willReturn(object)`.

    given(willReturn(object), list).get(1);

If you want to stub `void` method to just return without throwing, use `willReturn(null)`.

    given(willReturn(null)), list).clear();

Other thing that can happen on invocation is throwing `Throwable`.
For this purpose use `willThrow`.

    given(willThrow(new IndexOutOfBoundsException()), list).get(2);

Throwing throwable preserves original stack trace which may be inconvenient during debugging.
Using `willRethrow`
[fills in stack trace](http://docs.oracle.com/javase/7/docs/api/java/lang/Throwable.html#fillInStackTrace())
upon throwing.

    given(willRethrow(new IndexOutOfBoundsException()), list).get(2);

If you need more complex logic to happen on invocation, implement custom `Handler`.

    given((invocation -> {
      Method method = invocation.method;
      Object instance = invocation.instance;
      List<Object> arguments =  invocation.arguments;
      if(...) {
        return ...
      } else {
        throw ...
      }
    }), list).get(0);

If you stub a mock to return object incompatible with method return type,
or throw throwable incompatible with method signature,
you will get `TestoryException` upon invocation.

You can restub already stubbed invocation, because most recent stubbing takes precedence over earlier one.
Newly created mock is already stubbed for convenience.
`equals`/`hashCode` is stubbed, so mock is equal only to itself.
`toString` is stubbed to contain class name and unique ordinal.
All other methods are stubbed to return `null` (or binary zero for primitive types).
Those default stubbings can be restubbed as any other stubbing.

### Verifying

Additionally to being stubbable, mocks remember invocations that happened on them.
You can use this feature to assert your expectations about how tested object should collaborate with its dependencies.
Simplest way is to define your expectation by using `thenCalled` and providing mock, method and arguments of expected invocation.
For example, `FilterOutputStream` (tested class), when being closed, should also close `OutputStream` it decorates.

    given(output = mock(OutputStream.class));
    given(filterOutput = new FilterOutputStream(output));
    when(filterOutput).close();
    thenCalled(output).close();

By default, invocation is expected to be called exactly once.
Assertion fails if there are none or more than one matching invocations.
You can verify other number of invocations by passing exact value (may be 0).

    thenCalledTimes(3, mock).size();

You can also use matcher to be more flexible about number of invocations.

    thenCalledTimes(greaterThan(0), mock).toString();

By default, order of invocations does not matter.
If you need to assert that invocations happened in order, use ordered verifying.

    thenCalledInOrder(mockDatabase).open();
    thenCalledInOrder(mockDatabase).close();

### Matching Invocations

In previous examples of stubbing and verifying we always specified invocation by providing exact mock, method and arguments.
Sometimes you don't care about arguments.
It this situation, you can use `any` as a placeholder for arguments you don't care about.
For example, asserting that `add` method was called on mock `list` with any argument.

    thenCalled(list).add(any(Object.class));

If you don't want to specify exact argument, but still care about its value, you can provide matcher.

    thenCalled(list).add(any(Object.class, startsWith("prefix")));

`Class` passed to `any` is just for inferring purpose.
Argument can be instance of any type and still can match.

In most cases you can mix `any` with real arguments.

    thenCalled(list).add(0, any(Object.class));

In cases where you cannot (due to technical limitations of primitives) `TestoryException` is thrown.

    // throws TestoryException
    given(willReturn(true), mock).someMethod(any(int.class), intValue);

You can workaround those cases by wrapping primitive values in `a`.

    given(willReturn(true), mock).someMethod(any(int.class), a(intValue));

Use `the` shortcut if you expect exactly same instance

    given(willReturn(true), mock).someMethod(the(instance));

Above examples work well if you known exact mock and method of invocation.
Sometimes you want to be less specific than that.
You can take full control of matching invocations by implementing your own `InvocationMatcher`.
It is functional interface that answers, whether particular invocation is one you are interested about.
Using this interface you can stub or verify invocation on more than one method, or even more than one mock, at once.

    InvocationMatcher onCondition = new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        // custom logic
      }
    };

There are some built-in invocation matchers to support most popular idioms.
By convention they start with `on` prefix.
For example, you want to assert that there were no interactions on mock, meaning none of its methods was called.
Asserting that there were no invocations on each method one by one would be tedious.
You can achieve it using `onInstance` invocation matcher in combination with `thenCalledNever` assertion.

    thenCalledNever(onInstance(mock));

There are times when tested object has many collaborators and they are subjects to constant change.
You want to write test in manner that would not require to correct stubbings every time a method is moved from one collaborator to another.
This is usual for projects using service or dao objects.
Solution is to stub mocks in a way, that does not care about mock, but only about return type (and arguments).
See [komarro library](https://code.google.com/p/komarro/) for more on the topic.

    given(willReturn(person), onReturn(Person.class));
    given(willReturn(person), onRequest(Person.class, "username"));

### Spying

Spy is a mixture of real object and mock.
Technically it is a mock, which means it can be stubbed and verified like any other mock.
Difference is that at creation, spy is provided with spied object.
Any invocation on spy will be delegated to spied object, unless that invocation was restubbed.

Spies can be used in situations where you would like to assert that invocation happened on object that is not a mock.
Replacing object by mock would not achieve a goal, because object would lose its original non-mocky behavior.
For example, you test method that copies data from one stream to another.
You want to assert that method closes streams when done.
This would require streams to be mocks.
On the other hand input stream has to have real data, otherwise copy method would never reach the end of stream.
This is possible by spying on real streams.

    given(input = spy(new ByteArrayInputStream(new byte[] { 1, 2, 3 })));
    given(output = spy(new ByteArrayOutputStream()));
    when(copy(input, output));
    thenCalled(input).close();
    thenCalled(output).close();

Also you can stub an existing mock to act as a spy using `willSpy` handler.

    given(real = new ByteArrayInputStream(new byte[] { 1, 2, 3 }));
    given(input = mock(InputStream.class));
    given(willSpy(real), onInstance(input));

Real object does not have to be actually real, it can be other mock/spy.
There can be many spies for same real object.

# Utilities
[matchers](#matchers)

### Matchers

Wherever api method accepts `Object` via parameter named `matcher`, you are free to pass any compatible matcher

 - [org.hamcrest.Matcher](https://github.com/hamcrest/JavaHamcrest)
 - [org.fest.assertions.Condition](https://github.com/alexruiz/fest-assert-1.x)
 - [org.fest.assertions.core.Condition](https://github.com/alexruiz/fest-assert-2.x)
 - [org.assertj.core.api.Condition](https://github.com/joel-costigliola/assertj-core)
 - [com.google.common.base.Predicate](https://code.google.com/p/guava-libraries/)
 - [com.google.common.base.Function](https://code.google.com/p/guava-libraries/)
 - dynamic matcher

```
    Object matcher = new Object() {
      public boolean matches(Object item) {
        return ...;
      }
    };
```

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

### givenTest
(this feature is in beta)

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
[arrays](#arrays) | [primitives](#primitives) | [finals](#finals) | [purging](#purging) | [api](#api) | [class loader](#classloader)

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

Final classes cannot be mocked or chained. Any of the following throws exception.

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

### Purging
(this feature is in beta)

Testory maintains global state that holds information about every mock, stubbing and invocation.
This data needs to be periodically released to prevent running out of memory.
Since testory has no foolproof way to tell whether one test ended and another started, it relies on some simplistic assumptions

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

### API

Adding testory to your classpath gives you access to many public classes.
However, not all of them are intended for external use.
Thus, testory clarifies which classes are exposed as public API.
If class is exposed, you can rely on it's functionality along [major](http://semver.org/) version.

`org.testory.Testory` is exposed as it is the main entry point to library.
You make testory methods available by statically importing `Testory` class.

    import static org.testory.Testory.*;

If class is exposed, it recursively exposes all classes available through it's public methods.
It includes types of parameters, return types and annotations of those methods.

For sake of clarity, all exposed types are enumerated below.

 - `org.testory.Testory` - main entry point to library containing static methods
 - `org.testory.common.Closure` - functional interface representing piece of code returning `Object` or throwing `Throwable`
 - `org.testory.common.VoidClosure` - functional interface representing piece of code returning `void` or throwing `Throwable`
 - `org.testory.common.Nullable` - annotation that marks optional parameters and return values
 - `org.testory.proxy.Invocation` - represents invocation on mock (method, instance and arguments)
 - `org.testory.proxy.Handler` - represents logic executed when invoking method on mock
 - `org.testory.proxy.InvocationMatcher` - predicate for matching invocation on mock when you stub/verify

Deprecated classes that will be removed in next major version.

 - `org.testory.Closure` - deprecated alias for `org.testory.common.Closure`

Beta features are exception to those rules.
They can be removed at any time without incrementing major version.

### ClassLoader

Testory relies on generated bytecode to support some features, mocking in particular.
Bytecode is loaded using `ClassLoader`. Which class loader is used will affect loaded `Class` visibility and scope.
There are different strategies for class loader selection observed in different libraries.
The one testory chose is to always use `Thread.currentThread().getContextClassLoader()`.
This way caller has full control and full responsibility for class loading by setting context class loader using `Thread.currentThread().setContextClassLoader()`.

When you mock a class, testory uses class loader you provided to load generated bytecode.
This bytecode has a form of a class that extends class you mocked.
Class loader you provided will try to recursively find all parent classes in hierarchy.
For this to succeed, parent classes need to be visible from your class loader.
This requires your class loader to be same as, or child of, class loader that loaded parent class.
Failing to properly set class loader may cause some runtime errors, like
`NoClassDefFoundError, package *** does not exist` or
`java.lang.ClassNotFoundException: org.testory.external.net.sf.cglib.proxy.$Factory`

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
