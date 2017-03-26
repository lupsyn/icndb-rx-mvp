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

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icndb.domain.model.JokeItem;
import com.icndb.joke.ApplicationComponent;
import com.icndb.joke.JokeApp;
import com.icndb.joke.R;
import com.icndb.joke.base.BaseActivity;
import com.icndb.joke.neverending.adapters.RecyclerViewScrollListener;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.icndb.joke.utils.AppConstants.VIEW_STATE_EXPLICIT_CHECKED;
import static com.icndb.joke.utils.AppConstants.VIEW_STATE_THIRD_ACTIVITY;

public class ThirdActivity extends BaseActivity implements NeverPresenterImpl.View {
    @Inject
    NeverPresenterImpl neverPresenter;
    //
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_three)
    ProgressBar progressBar;

    //
    private DemoAdapter adapter;
    private ThirdActivityComponent component;
    private Boolean notExplicit = false;
    //
    private final List<JokeItem> items = new ArrayList<>();
    private final PublishRelay<Void> notify = PublishRelay.create();
    private final PublishRelay<Boolean> notifyExplicit = PublishRelay.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        ButterKnife.bind(this);
        notExplicit = getIntent().getBooleanExtra(VIEW_STATE_EXPLICIT_CHECKED, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        recyclerView.setAdapter(adapter);
        neverPresenter.register(this);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                notify.call(null);
            }
        });

        if (savedInstanceState != null) {
            ViewState savedState = savedInstanceState.getParcelable(VIEW_STATE_THIRD_ACTIVITY);
            if (savedState != null) {
                notExplicit = savedState.notExplicit;
                showJokes(savedState.items);
                showMainRefreshing(false);
            } else {
                notify.call(null);
            }
        } else {
            notify.call(null);
        }
        notifyExplicit.call(notExplicit);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(VIEW_STATE_THIRD_ACTIVITY, new ViewState(items, notExplicit));
        super.onSaveInstanceState(outState);

    }


    private void setupAdapter() {
        adapter = new DemoAdapter(this);
        adapter.setHasStableIds(true);
        adapter.setItems(items);
    }

    @Override
    protected void injectDependencies(JokeApp application, ApplicationComponent component) {
        getThirdActivityComponent(component).inject(this);
    }

    private ThirdActivityComponent getThirdActivityComponent(ApplicationComponent appComponent) {
        if (component == null) {
            component = appComponent.plus(new NeverModule());
        }
        return component;
    }

    @Override
    protected void releaseSubComponents(JokeApp application) {
        component = null;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(ThirdActivity.this, error, Toast.LENGTH_LONG).show();
    }


    @Override
    public void showJokes(List<JokeItem> jokes) {
        items.addAll(jokes);
        adapter.setItems(items); // No need of this
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        adapter.showLoading(isRefreshing);
    }

    @Override
    public void showMainRefreshing(boolean isRefreshing) {
        progressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<Void> onRefresh() {
        return notify;
    }

    @Override
    protected void onDestroy() {
        neverPresenter.unregister();
        super.onDestroy();
    }

    @Override
    public Observable<Boolean> onExplicitChecked() {
        return notifyExplicit;
    }
}
