/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017 Enrico Bruno Del Zotto
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.icndb.joke.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;


public class LocalResponseDispatcher extends QueueDispatcher {


    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        MockResponse mockResponse = new MockResponse();
        String scenario = getScenario(request);
        if (scenario != null) {
            try {
                mockResponse.setBody(readFile(scenario));
                mockResponse.setResponseCode(200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mockResponse;
    }

    private String getScenario(RecordedRequest request) {
        String scenario = "";

        String path = request.getPath();
        String requestedMethod = request.getMethod().toLowerCase(Locale.US);
        //	GET /jokes/random?firstName=Enrico&lastname=zootto HTTP/1.1
        scenario = requestedMethod + path.replace("/", "_");
        scenario = scenario.replace("=", "_");
        scenario = scenario.replace("?", "_");
        scenario = scenario.replace("&", "_");
        scenario = scenario + ".json";

        return scenario;
    }

    private String readFile(String jsonFileName) throws IOException {
        InputStream inputStream = LocalResponseDispatcher.class.getResourceAsStream("/"
                + jsonFileName);
        if (inputStream == null) {
            throw new NullPointerException("Have you added the local resource correctly?, "
                    + "Hint: name it as: " + jsonFileName);
        }
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(inputStream);
            BufferedReader rdr = new BufferedReader(isr);
            for (int c; (c = rdr.read()) != -1; ) {
                sb.append((char) c);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            if (isr != null) {
                isr.close();
            }
        }
        return sb.toString();
    }
}

