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

package com.icndb.joke.textinput;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ViewState implements Parcelable {
    final String inputText;
    final String showedValue;
    final Boolean notExplicit;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.inputText);
        dest.writeString(this.showedValue);
        dest.writeValue(this.notExplicit);
    }

    protected ViewState(Parcel in) {
        this.inputText = in.readString();
        this.showedValue = in.readString();
        this.notExplicit = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<ViewState> CREATOR = new Creator<ViewState>() {
        @Override
        public ViewState createFromParcel(Parcel source) {
            return new ViewState(source);
        }

        @Override
        public ViewState[] newArray(int size) {
            return new ViewState[size];
        }
    };
}
