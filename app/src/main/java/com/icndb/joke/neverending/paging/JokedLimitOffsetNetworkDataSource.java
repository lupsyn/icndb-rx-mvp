package com.icndb.joke.neverending.paging;

import android.arch.paging.DataSource;
import android.arch.paging.TiledDataSource;

import com.icndb.domain.model.JokeItem;

import java.util.List;

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
public class JokedLimitOffsetNetworkDataSource extends TiledDataSource<JokeItem> {
    JokedItemPagedListProvider.AdapterInterface repository;

    public JokedLimitOffsetNetworkDataSource(JokedItemPagedListProvider.AdapterInterface repository) {
        this.repository = repository;
    }

    @Override
    public int countItems() {
        return DataSource.COUNT_UNDEFINED;
    }

    //Should go in background
    @Override
    public List<JokeItem> loadRange(int startPosition, int count) {
        return repository.getJokesPagedAdapter(startPosition,count);
    }
}
