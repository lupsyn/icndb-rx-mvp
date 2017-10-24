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
package com.icndb.joke.interactors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.icndb.domain.api.IcndbService;
import com.icndb.domain.api.model.ApiJokeListResponse;
import com.icndb.domain.api.model.ApiJokeSingleResponse;
import com.icndb.domain.model.JokeItem;
import com.icndb.domain.model.JokeMapper;
import com.icndb.domain.utils.SchedulerProvider;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JokeIteractorTest {

    JokeInteractor interactor;
    IcndbService api;
    SchedulerProvider scheduler;

    ApiJokeSingleResponse singleResponse;
    JokeItem expectedSingleResponse;

    ApiJokeSingleResponse singleInputResponse;
    JokeItem expectedSingleInputResponse;

    ApiJokeListResponse listResponse;
    List<JokeItem> expectedListResponse;

    @Before
    public void setup() {
        api = mock(IcndbService.class);
        scheduler = mock(SchedulerProvider.class);


        Gson gson = new Gson();
        Type type = new TypeToken<List<JokeItem>>() {
        }.getType();

        try {

            String jsonSingleMockResult = readFile("get_jokes_random.json");
            singleResponse = gson.fromJson(jsonSingleMockResult, ApiJokeSingleResponse.class);
            expectedSingleResponse = gson.fromJson(readFile("parsed_random_result.json"), JokeItem.class);

            String jsonMockTextInputResult = readFile("get_jokes_random_firstName_enrico_lastname_del_zotto.json");
            singleInputResponse = gson.fromJson(jsonMockTextInputResult, ApiJokeSingleResponse.class);
            expectedSingleInputResponse = gson.fromJson(readFile("parsed_random_name_result.json"), JokeItem.class);

            String jsonMockListRandomResult = readFile("get_jokes_random_20.json");
            listResponse = gson.fromJson(jsonMockListRandomResult, ApiJokeListResponse.class);
            expectedListResponse = gson.<ArrayList<JokeItem>>fromJson(readFile("parsed_random_20.json"), type);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //mock scheduler to run immediately
        when(scheduler.mainThread())
                .thenReturn(Schedulers.immediate());
        when(scheduler.backgroundThread())
                .thenReturn(Schedulers.immediate());
        // mock api result with expected result
        when(api.getSingleRandom(any(String.class))).thenReturn(Observable.just(singleResponse));
        when(api.getSingleJokeWithCustomName(any(String.class), any(String.class), any(String.class))).thenReturn(Observable.just(singleInputResponse));
        when(api.getListJokes(any(String.class))).thenReturn(Observable.just(listResponse));
        interactor = new JokeInteractorImpl(api, scheduler, new JokeMapper());
    }

    @Test
    public void testGetRandom() throws Exception {
        TestSubscriber<JokeItem> testSubscriber = new TestSubscriber<>();
        interactor.getSingleRandom(false)
                .subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList(expectedSingleResponse));
    }

    @Test
    public void testGetRandomInputText() throws Exception {
        TestSubscriber<JokeItem> testSubscriber = new TestSubscriber<>();
        interactor.getSingleJokeWithCustomName("Enrico", "Del Zotto", false)
                .subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList(expectedSingleInputResponse));
    }

    @Test
    public void testGetRandomList() throws Exception {
        TestSubscriber<List<JokeItem>> testSubscriber = new TestSubscriber<>();
        interactor.getListJokes(false)
                .subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList(expectedListResponse));
    }

    private String readFile(String jsonFileName) throws IOException {
        InputStream inputStream = JokeInteractorImpl.class.getResourceAsStream("/"
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
