package com.tzutalin.dlib;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by houzhi on 16-10-20.
 */

public class FaceDet {

    private static final String TAG = "FaceDet";
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

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detFace(@NonNull final String path) {
        VisionDetRet[] detRets = jniFaceDet(path);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detFaceLandmark(@NonNull String path, @NonNull String landmarkModelPath) {
        VisionDetRet[] detRets = jniFaceLandmarkDet(path, landmarkModelPath);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detBitmapFaceLandmark(@NonNull Bitmap bitmap, @NonNull String landmarkModelPath) {
        VisionDetRet[] detRets = jniBitmapFaceLandmarkDet(bitmap, landmarkModelPath);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detBitmapFace(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = jniBitmapFaceDet(bitmap);
        return Arrays.asList(detRets);
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


    private native VisionDetRet[] jniFaceLandmarkDet(String path, String landmarkModelPath);

    private native VisionDetRet[] jniBitmapFaceLandmarkDet(Bitmap bitmap, String landmarkModelPath);

    private native VisionDetRet[] jniBitmapFaceDet(Bitmap bitmap);

    private native VisionDetRet[] jniFaceDet(String path);

}