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

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

import java.lang.reflect.Method;


public class CustomTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader classLoader, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // replace Application class with mock one
        return super.newApplication(classLoader, JokeTestApp.class.getName(), context);
    }

    /*
     *Fix android emulator issues on CIs
     */
    @Override
    public void onStart() {
        runOnMainSync(() -> {
            Context app = CustomTestRunner.this.getTargetContext().getApplicationContext();

            CustomTestRunner.this.disableAnimations(app);

            String name = CustomTestRunner.class.getSimpleName();
            unlockScreen(app, name);
            keepScreenAwake(app, name);
        });

        super.onStart();
    }


    @Override
    public void finish(int resultCode, Bundle results) {
        super.finish(resultCode, results);
        enableAnimations(getContext());
    }

    private void keepScreenAwake(Context app, String name) {
        PowerManager power = (PowerManager) app.getSystemService(Context.POWER_SERVICE);
        power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, name)
                .acquire();
    }

    private void unlockScreen(Context app, String name) {
        KeyguardManager keyguard = (KeyguardManager) app.getSystemService(Context.KEYGUARD_SERVICE);
        // keyguard.newKeyguardLock(name).disableKeyguard();
    }

    void disableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(android.Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(0.0f);
        }
    }

    void enableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(android.Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(1.0f);
        }
    }

    private void setSystemAnimationsScale(float animationScale) {
        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                currentScales[i] = animationScale;
            }
            setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
//            Timber.d("Changed permissions of animations");
        } catch (Exception e) {
//            Timber.e("Could not change animation scale to %s", animationScale);
        }
    }

}
