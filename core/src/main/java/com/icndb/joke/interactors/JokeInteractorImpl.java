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

package com.icndb.joke.interactors;

import com.icndb.domain.api.IcndbService;
import com.icndb.domain.model.JokeItem;
import com.icndb.domain.model.JokeMapper;
import com.icndb.domain.utils.SchedulerProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class JokeInteractorImpl implements JokeInteractor {
    private JokeMapper jokeMapper;
    private IcndbService jokeApi;
    private SchedulerProvider schedulerProvider;

    @Inject
    public JokeInteractorImpl(IcndbService jokeApi,
                              SchedulerProvider schedulerProvider,
                              JokeMapper jokeMapper) {
        this.jokeApi = jokeApi;
        this.schedulerProvider = schedulerProvider;
        this.jokeMapper = jokeMapper;
    }

    @Override
    public Observable<JokeItem> getSingleRandom(Boolean explicit) {
        List<String> args = new ArrayList<>();
        if (explicit) {
            args.add("explicit");
        }
        return jokeApi.getSingleRandom(args.size() > 0 ? Arrays.toString(args.toArray()) : null)
                .map(jokeMapper::map)
                .subscribeOn(schedulerProvider.backgroundThread());
    }

    @Override
    public Observable<JokeItem> getSingleJokeWithCustomName(String firstName, String lastname, Boolean explicit) {
        List<String> args = new ArrayList<>();
        if (explicit) {
            args.add("explicit");
        }

        return jokeApi.getSingleJokeWithCustomName(firstName, lastname, args.size() > 0 ? Arrays.toString(args.toArray()) : null)
                .map(jokeMapper::map)
                .subscribeOn(schedulerProvider.backgroundThread());
    }

    @Override
    public Observable<List<JokeItem>> getListJokes(Boolean explicit) {
        List<String> args = new ArrayList<>();
        if (explicit) {
            args.add("explicit");
        }
        return jokeApi.getListJokes(args.size() > 0 ? Arrays.toString(args.toArray()) : null)
                .map(jokeMapper::map)
                .subscribeOn(schedulerProvider.backgroundThread());
    }
}
