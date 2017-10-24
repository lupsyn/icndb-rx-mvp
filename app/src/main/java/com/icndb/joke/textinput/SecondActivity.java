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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icndb.domain.model.JokeItem;
import com.icndb.joke.ApplicationComponent;
import com.icndb.joke.JokeApp;
import com.icndb.joke.R;
import com.icndb.joke.base.BaseActivity;
import com.jakewharton.rxrelay2.PublishRelay;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static com.icndb.joke.utils.AppConstants.VIEW_STATE_EXPLICIT_CHECKED;
import static com.icndb.joke.utils.AppConstants.VIEW_STATE_SECOND_ACTIVITY;


public class SecondActivity extends BaseActivity implements TextPresenterImpl.View {
    @BindView(R.id.text_input_edit_text)
    EditText inputText;
    @BindView(R.id.bt_submit)
    Button submitButton;
    @BindView(R.id.progress_bar_second)
    ProgressBar progressBar;

    @Inject
    TextPresenterImpl textPresenter;

    private final PublishRelay<Boolean> notifyExplicit = PublishRelay.create();
    private final PublishRelay<String> notify = PublishRelay.create();
    private SecondActivityComponent component = null;
    private Boolean notExplicit = false;
    private String showedValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
        notExplicit = getIntent().getBooleanExtra(VIEW_STATE_EXPLICIT_CHECKED, false);
        textPresenter.register(this);

//        RxView.clicks(submitButton)
//                .debounce(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//                .map(ignored -> inputText.getText().toString())
//                .subscribe(notify::call);


        if (savedInstanceState != null) {
            ViewState savedState = savedInstanceState.getParcelable(VIEW_STATE_SECOND_ACTIVITY);
            if (savedState != null) {
                notExplicit = savedState.notExplicit;
                inputText.setText(savedState.inputText);
                if (savedState.showedValue != null) {
                    showInternalDialog(savedState.showedValue);
                }
            }
        }
        notifyExplicit.accept(notExplicit);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(VIEW_STATE_SECOND_ACTIVITY, new ViewState(inputText.getText().toString(), showedValue, notExplicit));
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void injectDependencies(JokeApp application, ApplicationComponent component) {
        getSecondActivityComponent(component).inject(this);
    }


    private SecondActivityComponent getSecondActivityComponent(ApplicationComponent appComponent) {
        if (component == null) {
            component = appComponent.plus(new TextModule());
        }
        return component;
    }

    @Override
    protected void releaseSubComponents(JokeApp application) {
        component = null;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(SecondActivity.this, error, Toast.LENGTH_LONG).show();
    }


    @Override
    public void showJoke(JokeItem joke) {
        showedValue = joke.getValue();
        showInternalDialog(showedValue);
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        progressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<String> onSubmitButton() {
        return notify;
    }

    @Override
    public Observable<Boolean> onExplicitChecked() {
        return notifyExplicit;
    }

    @Override
    protected void onDestroy() {
        textPresenter.unregister();
        super.onDestroy();
    }


    private void showInternalDialog(String value) {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.title_text_input_joke) + " " + inputText.getText().toString())
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
