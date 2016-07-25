/*
*  Copyright (C) 2015 TzuTaLin
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.tzutalin.dlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tzutalin on 2015/10/20.
 */
public class PeopleDet {
    private static final String TAG = "PeopleDet";
    protected static boolean sInitialized = false;

    static {
        try {
            System.loadLibrary("people_det");
            jniNativeClassInit();
            sInitialized = true;
            Log.d(TAG, "jniNativeClassInit success");
        } catch (UnsatisfiedLinkError e) {
            android.util.Log.d("PeopleDet", "library not found!");
        }
    }

    protected Context mContext;

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detPerson(@NonNull final String path) {
        List<VisionDetRet> ret = new ArrayList<VisionDetRet>();
        int size = jniOpencvHOGDetect(path);
        Log.d(TAG, "detPerson size " + size);
        for (int i = 0; i != size; i++) {
            Log.d(TAG, "enter vision loop");
            VisionDetRet det = new VisionDetRet();
            int success = jniGetOpecvHOGRet(det, i);
            Log.d(TAG, "detPerson success " + success);
            if (success >= 0) {
                Log.d(TAG, "detPerson rect " + det.toString());
                ret.add(det);
            }
        }
        return ret;
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detFace(@NonNull final String path, @NonNull String landmarkModelPath) {
        List<VisionDetRet> ret = new ArrayList<VisionDetRet>();
        int size = jniDLibHOGFaceDetect(path, landmarkModelPath);
        for (int i = 0; i != size; i++) {
            VisionDetRet det = new VisionDetRet();
            int success = jniGetDLibHOGFaceRet(det, i);
            if (success >= 0) {
                ret.add(det);
            }
        }
        return ret;
    }

    //Author:zhao
    //Mail:zhaotu2016@163.com
    //Date:2016/5/10

    /**
     * Input is bitmap
     *
     * @param bitmap
     * @return The list of VisionDetRets
     */
    @NonNull
    public List<VisionDetRet> detBitmapFace(@NonNull Bitmap bitmap, @NonNull String landmarkModelPath) {
        List<VisionDetRet> ret = new ArrayList<VisionDetRet>();
        int size = jniBitmapFaceDect(bitmap, landmarkModelPath);
        for (int i = 0; i != size; i++) {
            VisionDetRet det = new VisionDetRet();
            int success = jniGetDLibHOGFaceRet(det, i);
            if (success >= 0) {
                ret.add(det);
            }
        }
        return ret;
    }


    public void init() {
        jniInit();
    }

    public void deInit() {
        jniDeInit();
    }

    private native static void jniNativeClassInit();

    private native int jniInit();

    private native int jniDeInit();

    private native int jniOpencvHOGDetect(String path);

    private native int jniGetOpecvHOGRet(VisionDetRet det, int index);

    private native int jniDLibHOGDetect(String path, String svmModelPath);

    private native int jniGetDLibHOGRet(VisionDetRet det, int index);

    private native int jniDLibHOGFaceDetect(String path, String landmarkModelPath);

    private native int jniGetDLibHOGFaceRet(VisionDetRet det, int index);

    //Bitmap detection JNI
    private native int jniBitmapFaceDect(Bitmap bitmap, String landmarkModelPath);

}
