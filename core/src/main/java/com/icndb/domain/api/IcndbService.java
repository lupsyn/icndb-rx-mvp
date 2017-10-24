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
package com.icndb.domain.api;


import com.icndb.domain.api.model.ApiJokeListResponse;
import com.icndb.domain.api.model.ApiJokeSingleResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IcndbService {
    @GET("jokes/random")
    Observable<ApiJokeSingleResponse> getSingleRandom(@Query("exclude") String parms);

    @GET("jokes/random")
    Observable<ApiJokeSingleResponse> getSingleJokeWithCustomName(@Query("firstName") String firstName,
                                                                  @Query("lastname") String lastname,
                                                                  @Query("exclude") String parms);

    @GET("jokes/random/20")
    Observable<ApiJokeListResponse> getListJokes(@Query("exclude") String parms);

    @GET("jokes/random/{multipleItemCount}")
    Observable<ApiJokeListResponse> getMulipleListJokes(@Path("multipleItemCount") int countNumber);
}
