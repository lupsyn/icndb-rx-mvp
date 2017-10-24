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
package com.icndb.domain.model;

import com.icndb.domain.api.model.ApiJoke;
import com.icndb.domain.api.model.ApiJokeListResponse;
import com.icndb.domain.api.model.ApiJokeSingleResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JokeMapper {

    public List<JokeItem> map(ApiJokeListResponse response) {
        List<JokeItem> toReturn = new ArrayList<>();
        for (ApiJoke apiJoke : response.value) {
            toReturn.add(JokeItem.builder().value(apiJoke.joke).build());
        }
        return toReturn;
    }

    public JokeItem map(ApiJokeSingleResponse response) {
        return JokeItem.builder().value(response.value.joke).build();
    }
}
