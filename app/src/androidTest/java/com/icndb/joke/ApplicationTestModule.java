/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Enrico Bruno Del Zotto
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.icndb.joke;

import android.support.annotation.NonNull;

import com.icndb.joke.utils.LocalResponseDispatcher;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;

@Module
public class ApplicationTestModule extends ApplicationModule {

    private final LocalResponseDispatcher dispatcher;
    private final MockWebServer mockWebServer;


    public ApplicationTestModule() {
        dispatcher = new LocalResponseDispatcher();
        this.mockWebServer = getMockWebServer(dispatcher);
    }

    @Provides
    @Singleton
    public MockWebServer provideDefaultMockWebServer(Dispatcher dispatcher) {
        return mockWebServer;
    }

    @Override
    protected String getBaseUrl() {
        return mockWebServer.url("/").toString();
    }

    @NonNull
    private MockWebServer getMockWebServer(Dispatcher dispatcher) {

        MockWebServer mockWebServer = new MockWebServer();
        new Thread(() -> {
            try {
                mockWebServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        mockWebServer.setDispatcher(dispatcher);


        return mockWebServer;
    }


    @Provides
    @Singleton
    public Dispatcher getTestDispatcher() {
        return dispatcher;
    }

}
