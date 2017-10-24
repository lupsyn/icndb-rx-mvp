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
package com.icndb.domain;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class ClientModule {

    private static final String HTTP_CACHE_PATH = "http-cache";
    private static final String CACHE_CONTROL = "Cache-Control";

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(@Named("networkTimeoutInSeconds") int networkTimeoutInSeconds,
                                            @Named("isDebug") boolean isDebug,
                                            Cache cache,
                                            HttpLoggingInterceptor loggingInterceptor,
                                            @Named("cacheInterceptor") Interceptor cacheInterceptor

    ) {


        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .connectTimeout(networkTimeoutInSeconds, TimeUnit.SECONDS);
        //show logs if app is in Debug mode
        if (isDebug)
            okHttpClient.addInterceptor(loggingInterceptor);

        return okHttpClient.build();
    }

    @Singleton
    @Provides
    @Named("cacheInterceptor")
    public Interceptor provideCacheInterceptor(@Named("cacheMaxAge") int maxAgeMin) {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(maxAgeMin, TimeUnit.MILLISECONDS)
                    .build();

            return response.newBuilder()
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    @Singleton
    @Provides
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    @Provides
    @Singleton
    public Cache provideCache(@Named("cacheDir") File cacheDir, @Named("cacheSize") long cacheSize) {
        Cache cache = null;
        try {
            cache = new Cache(new File(cacheDir.getPath(), HTTP_CACHE_PATH), cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cache;
    }


    @Singleton
    @Provides
    @Named("retryInterceptor")
    public Interceptor provideRetryInterceptor(@Named("retryCount") int retryCount) {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;

            int tryCount = 0;
            while (tryCount < retryCount && (null == response || !response.isSuccessful())) {
                // retry the request
                try {
                    response = chain.proceed(request);
                } catch (IOException e) {
                    exception = e;
                } finally {
                    tryCount++;
                }
            }

            // throw last exception
            if (null == response && null != exception)
                throw exception;

            // otherwise just pass the original response on
            return response;
        };
    }
}
