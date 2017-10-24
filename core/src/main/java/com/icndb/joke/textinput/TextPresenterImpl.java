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
package com.icndb.joke.textinput;

import com.icndb.base.BaseView;
import com.icndb.domain.model.JokeItem;
import com.icndb.domain.utils.SchedulerProvider;
import com.icndb.domain.utils.name.HumanNameParserParser;
import com.icndb.domain.utils.name.Name;
import com.icndb.joke.interactors.JokeInteractor;

import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;


public class TextPresenterImpl implements TextPresenter {
    private final JokeInteractor interactor;
    private final SchedulerProvider scheduleProvider;
    private TextPresenterImpl.View view;
    private final CompositeDisposable compositeSubscription = new CompositeDisposable();

    private boolean checkedValue = false;


    @Inject
    public TextPresenterImpl(JokeInteractor interactor, SchedulerProvider scheduleProvider) {
        this.interactor = interactor;
        this.scheduleProvider = scheduleProvider;
    }

    @Override
    public void register(View view) {
        if (this.view != null) {
            throw new IllegalStateException("View " + this.view + " is already attached. Cannot attach " + view);
        }
        this.view = view;

        addToUnsubscribe(view.onSubmitButton()
                .doOnNext(ignored -> view.showRefreshing(true))
                .flatMap(s -> Observable.just(s).map(value -> {
                    if (value.equals("")) {
                        throw Exceptions.propagate(new Exception("Full name can't be null!"));
                    }
                    //Regex pattern for matching only alphabets and white spaces
                    if (Pattern.matches("^[a-zA-Z]+[\\-'\\s]?[a-zA-Z ]+$", value)) {
                        return value;
                    } else {
                        throw Exceptions.propagate(new Exception("Enter a correct full name!"));
                    }
                }).doOnError(throwable -> {
                    view.showRefreshing(false);
                    view.showError(throwable.getMessage());
                }).onErrorResumeNext(Observable.<String>empty()))
                .flatMap(fullname -> Observable.just(fullname).switchMap(value -> {
                    Name object = new Name(fullname);
                    HumanNameParserParser parser = new HumanNameParserParser(object);
                    return interactor.getSingleJokeWithCustomName(parser.getFirst(), parser.getLast(), checkedValue);
                })
                        .doOnError(throwable -> {
                            view.showRefreshing(false);
                            view.showError(throwable.getMessage());
                        }).onErrorResumeNext(Observable.<JokeItem>empty()))
                .observeOn(scheduleProvider.mainThread())
                .subscribe(jokeItem -> {
                            view.showRefreshing(false);
                            view.showJoke(jokeItem);
                        }, throwable -> {
                            view.showRefreshing(false);
                            view.showError("Error to retrive : " + throwable.getMessage());
                        }
                ));

        addToUnsubscribe(view.onExplicitChecked().observeOn(scheduleProvider.mainThread())
                .subscribe(value -> checkedValue = value,
                        throwable -> {
                            if (null != view) {
                                view.showError(throwable.getMessage());
                            }
                        }));
    }

    @Override
    public void unregister() {
        if (view == null) {
            throw new IllegalStateException("View is already detached");
        }
        view = null;
        compositeSubscription.clear();
    }

    public interface View extends BaseView {
        void showJoke(JokeItem joke);

        void showRefreshing(boolean isRefreshing);

        Observable<String> onSubmitButton();

        Observable<Boolean> onExplicitChecked();

    }


    protected final void addToUnsubscribe(final Disposable subscription) {
        compositeSubscription.add(subscription);
    }
}
