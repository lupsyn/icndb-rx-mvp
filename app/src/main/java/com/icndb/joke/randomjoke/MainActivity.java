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
package com.icndb.joke.randomjoke;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icndb.domain.model.JokeItem;
import com.icndb.joke.ApplicationComponent;
import com.icndb.joke.JokeApp;
import com.icndb.joke.R;
import com.icndb.joke.base.BaseActivity;
import com.icndb.joke.neverending.ThirdActivity;
import com.icndb.joke.textinput.SecondActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.icndb.joke.utils.AppConstants.MAIN_VIEW;
import static com.icndb.joke.utils.AppConstants.MAIN_VIEW_SHOWED_TEXT;
import static com.icndb.joke.utils.AppConstants.VIEW_STATE_EXPLICIT_CHECKED;


public class MainActivity extends BaseActivity implements RandomPresenterImpl.View {
    @Inject
    RandomPresenterImpl mainPresenter;
    //
    @BindView(R.id.bt_random_joke)
    Button btRandomJoke;
    @BindView(R.id.bt_text_input)
    Button btTextInput;
    @BindView(R.id.bt_neverending_list)
    Button btNeverEndingList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.bt_checkbox)
    CheckBox btCheckbox;
    //
    private final PublishRelay<Void> notify = PublishRelay.create();
    private final PublishRelay<Boolean> notifyBoolean = PublishRelay.create();
    //
    private MainActivityComponent component = null;
    private Boolean notExplicit = false;
    private String showedValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainPresenter.register(this);

//        RxView.clicks(btRandomJoke)
//                .debounce(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//                .subscribe(notify::acceptboolea);

        if (savedInstanceState != null) {
            notExplicit = savedInstanceState.getBoolean(MAIN_VIEW);
            showedValue = savedInstanceState.getString(MAIN_VIEW_SHOWED_TEXT);
            btCheckbox.setChecked(notExplicit);
            notifyBoolean.accept(notExplicit);
            if (showedValue != null) {
                showInternalDialog(showedValue);
            }

        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MAIN_VIEW, notExplicit);
        outState.putString(MAIN_VIEW_SHOWED_TEXT, showedValue);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void injectDependencies(JokeApp application, ApplicationComponent appComponent) {
        getMainActivityComponent(appComponent).inject(this);
    }

    private MainActivityComponent getMainActivityComponent(ApplicationComponent appComponent) {
        if (component == null) {
            component = appComponent.plus(new RandomModule());
        }
        return component;
    }

    @Override
    protected void releaseSubComponents(JokeApp application) {
        component = null;
    }


    @OnCheckedChanged(R.id.bt_checkbox)
    public void onCheckBoxChanged(boolean isChecked) {
        notExplicit = isChecked;
        notifyBoolean.accept(notExplicit);

    }

    @OnClick(R.id.bt_text_input)
    public void onTextInputClicked(View view) {
        final Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra(VIEW_STATE_EXPLICIT_CHECKED, notExplicit);
        MainActivity.this.startActivity(intent);
    }

    @OnClick(R.id.bt_neverending_list)
    public void onNeverEndingListCliecked(View view) {
        final Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
        intent.putExtra(VIEW_STATE_EXPLICIT_CHECKED, notExplicit);
        MainActivity.this.startActivity(intent);
    }


    @Override
    public void showJoke(JokeItem joke) {
        showedValue = joke.getValue();
        showInternalDialog(showedValue);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Observable<Void> onRandomClicked() {
        return notify;
    }

    @Override
    public Observable<Boolean> onExplicitChecked() {
        return notifyBoolean;
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        progressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        mainPresenter.unregister();
        super.onDestroy();
    }

    private void showInternalDialog(String value) {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.bt_random_joke)
                .setMessage(value)
                .setNegativeButton(R.string.bt_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showedValue = null;
                    }
                })
                .show();
    }
}
