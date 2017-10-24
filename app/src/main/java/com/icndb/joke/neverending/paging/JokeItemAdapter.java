package com.icndb.joke.neverending.paging;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icndb.domain.model.JokeItem;
import com.icndb.joke.R;

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
public class JokeItemAdapter extends PagedListAdapter<JokeItem, JokeItemViewHolder> {

    public JokeItemAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public JokeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_third_list_item, parent, false);
        return new JokeItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JokeItemViewHolder holder, int position) {
        //Bind the item
        JokeItem item = getItem(position);
        if (item != null) {
            holder.bindTo(item);
        } else {
            holder.clear();
        }
    }

    private static final DiffCallback<JokeItem> DIFF_CALLBACK = new DiffCallback<JokeItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull JokeItem oldItem, @NonNull JokeItem newItem) {
            return oldItem.value == newItem.value;
        }

        @Override
        public boolean areContentsTheSame(@NonNull JokeItem oldItem, @NonNull JokeItem newItem) {
            return oldItem.value.equals(newItem.value);
        }
    };

}