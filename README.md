# icndb-rx-mvp
A showcase of RxJava and Model View Presenter, plus a number of other popular libraries for android development, 
including RxJava,RxRelay,Dagger2, Retrofit, ButterKnife,Lombok,MockWebServer.
Unit tests covering any business logic and Robolectric tests verifying the ui. 


## Benefits

This setup has a number of advantages over a non-MVP app architecture
 - it separates our concerns
    - the `Presenter` is view agnostic and does not care how an action was triggered, making a clear division which is easy to change
    - the view which implements the `View` interface is very simple - the methods are usually one liners, doing something on the android `Activity` e.g. just setting a view's state to `View.GONE` - which also makes them easy to test
 - it allows us to place all our business logic within the `Presenter` object and abstracts the `View` for easy mocking, so we can unit test all the things, e.g: 
    - when we're doing a network request, does the loading indicator show when it starts, and hide when it ends?
    - are we ignoring clicks on the 'refresh' button when a network call doing a refresh is already in progress?
    - what happens when a network call fails?
    - ... etc 
 - support for orientation changes (e.g. device rotation) with very little effort (currently view state is saved in a bundle and presenter is recreted, in another approch you can cache instance of the presenter)
 - the power of rxjava 
    - `Observable`s exposing future actions via the `View` interface, allowing our `Presenter`s to be entirely stateless
    - easy to do long running operations off the main thread
    - in app code but also in the unit tests, e.g the excellent `TestScheduler`  
    
## Implementation

 This project has a separate module called `mocks`. Our android application module `app` has a `testCompile`
 dependency on the `mocks` module.  (Injected with dagger)
 We have created a `LocalResponseDispatcher` which takes care of the local API needs,  we use `MockWebserver` to test the API calls.

 Also the testCompile in the `core` module depends on mocks in which we have stored .json response,
 in order to correctly test in Junit plain tests.




# License

MIT License

Copyright (c) 2017 Enrico Bruno Del Zotto

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
