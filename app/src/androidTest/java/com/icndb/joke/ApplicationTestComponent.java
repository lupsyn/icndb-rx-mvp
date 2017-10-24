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
package com.icndb.joke;


import com.icndb.domain.ApiModule;
import com.icndb.domain.ClientModule;
import com.icndb.joke.neverending.NeverModule;
import com.icndb.joke.neverending.ThirdActivityComponent;
import com.icndb.joke.randomjoke.MainActivityComponent;
import com.icndb.joke.randomjoke.RandomModule;
import com.icndb.joke.textinput.SecondActivityComponent;
import com.icndb.joke.textinput.TextModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AndroidModule.class,
        ApplicationTestModule.class,
        ApiModule.class,
        ClientModule.class
})

public interface ApplicationTestComponent extends ApplicationComponent {

    MainActivityComponent plus(RandomModule module);

    SecondActivityComponent plus(TextModule module);

    ThirdActivityComponent plus(NeverModule module);

}