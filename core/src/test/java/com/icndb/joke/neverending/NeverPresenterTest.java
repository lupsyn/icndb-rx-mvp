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

package com.icndb.joke.neverending;

import com.icndb.domain.model.JokeItem;
import com.icndb.domain.utils.SchedulerProvider;
import com.icndb.joke.interactors.JokeInteractor;

import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NeverPresenterTest {
    private NeverPresenterImpl presenter;
    private JokeInteractor interactor;
    private SchedulerProvider scheduleProvider;
    private NeverPresenterImpl.View view;

    private final PublishSubject<Void> jokeRefresh = PublishSubject.create();
    private final PublishSubject<Boolean> booleanValue = PublishSubject.create();
    private List<JokeItem> stub = new ArrayList<>();

    @Before
    public void setup() {
        JokeItem item = JokeItem.builder().value("31337").build();
        stub.add(item);
        interactor = mock(JokeInteractor.class);
        scheduleProvider = mock(SchedulerProvider.class);
        view = mock(NeverPresenterImpl.View.class);
        // mock scheduler to run immediately

        when(scheduleProvider.mainThread())
                .thenReturn(Schedulers.immediate());
        when(scheduleProvider.backgroundThread())
                .thenReturn(Schedulers.immediate());

        when(view.onExplicitChecked()).thenReturn(booleanValue);
        when(view.onRefresh()).thenReturn(jokeRefresh);

        presenter = new NeverPresenterImpl(interactor, scheduleProvider);
    }


    @Test
    public void onViewAttachedAndRefreshed() {
        presenter.register(view);
        verify(view).showMainRefreshing(anyBoolean());
    }

    @Test
    public void onRefreshAsked() {
        when(interactor.getListJokes(anyBoolean())).thenReturn(Observable.just(stub));

        presenter.register(view);
        jokeRefresh.onNext(null);
        verify(view,times(2)).showRefreshing(anyBoolean());
        verify(interactor).getListJokes(anyBoolean());
        verify(view).showMainRefreshing(false);
        verify(view).showRefreshing(false);
        verify(view).showJokes(stub);
    }

    @Test
    public void onNetworkError() {


        when(interactor.getListJokes(anyBoolean()))
                .thenReturn(
                        Observable.<List<JokeItem>>error(
                                new HttpException(
                                        Response.error(HttpURLConnection.HTTP_NOT_FOUND,
                                                ResponseBody.create(
                                                        MediaType.parse("God Knows What?"),
                                                        "Why am I even alive?")))));
        presenter.register(view);
        jokeRefresh.onNext(null);

        verify(view).showRefreshing(false);
        verify(view).showRefreshing(false);
        verify(view).showError("HTTP 404 null");

    }
}
