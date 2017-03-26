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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icndb.domain.model.JokeItem;
import com.icndb.joke.R;
import com.icndb.joke.neverending.adapters.FooterLoaderAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DemoAdapter extends FooterLoaderAdapter<JokeItem> {

    public DemoAdapter(Context context) {
        super(context);
    }

    @Override
    public long getYourItemId(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder getYourItemViewHolder(ViewGroup parent) {
        return new DemoViewHolder(inflater.inflate(R.layout.activity_third_list_item, parent, false));
    }

    @Override
    public void bindYourViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DemoViewHolder) {
            DemoViewHolder viewHolder = (DemoViewHolder) holder;
            viewHolder.textField.setText(items.get(position).getValue());
        }
    }

    public class DemoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_item)
        TextView textField;

        public DemoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
