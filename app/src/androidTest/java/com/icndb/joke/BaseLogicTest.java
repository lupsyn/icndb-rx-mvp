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

import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;

public class BaseLogicTest {
    @Inject
    MockWebServer mockWebServer;
    @Inject
    Dispatcher dispatcher;


    protected void setResponse(MockResponse response) {
        QueueDispatcher dispathcer = new QueueDispatcher();
        dispathcer.enqueueResponse(response);
        mockWebServer.setDispatcher(dispathcer);
    }

    protected MockWebServer getMockWebServer() {
        return mockWebServer;
    }


    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) {
            mockWebServer.setDispatcher(dispatcher);
            mockWebServer.shutdown();
        }
    }

}

