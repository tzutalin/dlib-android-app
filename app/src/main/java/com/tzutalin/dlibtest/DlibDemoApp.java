/*
 * Copyright (c) 2017. Tzutalin
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tzutalin.dlibtest;

import android.app.Application;
import android.util.Log;

import timber.log.Timber;

/**
 * Created by tzutalin on 2017/2/23.
 */
public class DlibDemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            //Timber.plant(new DebugLogFileTree(Environment.getExternalStorageDirectory().toString()));
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    /**
     * A tree which logs important information
     */
    private static class ReleaseTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            super.log(priority, tag, message, t);
        }
    }
}
