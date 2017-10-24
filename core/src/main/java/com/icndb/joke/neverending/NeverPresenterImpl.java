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
package com.icndb.joke.neverending;

import com.icndb.base.BaseView;
import com.icndb.domain.model.JokeItem;
import com.icndb.domain.utils.SchedulerProvider;
import com.icndb.joke.interactors.JokeInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class NeverPresenterImpl implements NeverPresenter {
    private final JokeInteractor interactor;
    private final SchedulerProvider scheduleProvider;
    private NeverPresenterImpl.View view;
    private final CompositeDisposable compositeSubscription = new CompositeDisposable();

    private boolean checkedValue = false;


    @Inject
    public NeverPresenterImpl(JokeInteractor interactor, SchedulerProvider scheduleProvider) {
        this.interactor = interactor;
        this.scheduleProvider = scheduleProvider;
    }

    @Override
    public void register(View view) {
        if (this.view != null) {
            throw new IllegalStateException("View " + this.view + " is already attached. Cannot attach " + view);
        }
        this.view = view;
        view.showMainRefreshing(true);
        addToUnsubscribe(view.onRefresh()
                .doOnNext(ignored -> view.showRefreshing(true))

                .flatMap(jokes -> Observable.just(jokes).switchMap(value -> {
                    return interactor.getListJokes(checkedValue);
                })
                        .doOnError(throwable -> {
                            view.showMainRefreshing(false);
                            view.showRefreshing(false);
                            view.showError(throwable.getMessage());
                        }).onErrorResumeNext(Observable.<List<JokeItem>>empty()))
                .observeOn(scheduleProvider.mainThread())
                .subscribe(jokes -> {
                            view.showMainRefreshing(false);
                            view.showRefreshing(false);
                            view.showJokes(jokes);
                        },
                        throwable -> {
                            if (null != view) {
                                view.showMainRefreshing(false);
                                view.showRefreshing(false);
                                view.showError(throwable.getMessage());
                            }
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
        Observable<Boolean> onExplicitChecked();

        void showJokes(List<JokeItem> joke);

        void showRefreshing(boolean isRefreshing);

        void showMainRefreshing(boolean isRefreshing);

        Observable<Void> onRefresh();


    }


    protected final void addToUnsubscribe(final Disposable subscription) {
        compositeSubscription.add(subscription);
    }
}
