Testory makes your tests human-readable by using given-when-then idiom advised by [Behavior Driven Development](http://en.wikipedia.org/wiki/Behavior_Driven_Development) .

With Testory you can decorate your code adding **given**, **when** and **then** keywords, where **then** works like junit's assertTrue.

        given(list = new ArrayList<String>());
        when(list.add("element"));
        then(!list.isEmpty());

You can assert value returned in **when** ...

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.get(0));
        thenReturned("element");

or assert that exception was thrown.

        given(list = new ArrayList<String>());
        when(list).get(0);
        thenThrown(IndexOutOfBoundsException.class);

You can stub a mock to return Object or throw Throwable ...

        given(list = mock(List.class));
        given(willReturn(object), list).get(1);
        given(willThrow(new IndexOutOfBoundsException()), list).get(2);

and verify call.

        given(output = mock(OutputStream.class));
        given(filterOutput = new FilterOutputStream(output));
        when(filterOutput).close();
        thenCalled(output).close();

Tests can be even more compact using matchers ...

        given(list = new ArrayList<String>());
        given(list.add("element"));
        when(list.clone());
        thenReturned(not(sameInstance(list)));

or handy macros.

        given(list = new ArrayList<String>());
        givenTimes(5, list).add("element");
        when(list.size());
        thenReturned(5);

See [Tutorial](doc/tutorial.md) for complete list of features.

[Testory on Github](https://github.com/maciejmikosik/testory) |
[Download release](https://github.com/maciejmikosik/testory/releases).
