package com.tzutalin.dlib;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by darrenl on 6/2/16.
 */
public class FaceLandmark {

    private ArrayList<Point> mLandmarkPoints = new ArrayList<>();

    private FaceLandmark() {

    }

    public FaceLandmark(final String labelStr) {
        // TODO : It should be passed to JNI. Curretly, workaround to parse label to get points
        if (labelStr.startsWith("face_landmarks ")) {
            String[] landmarkStrs = labelStr.replaceFirst("face_landmarks ", "").split(":");
            for (String landmarkStr : landmarkStrs) {
                String[] xyStrs = landmarkStr.split(",");
                int pointX = Integer.parseInt(xyStrs[0]);
                int pointY = Integer.parseInt(xyStrs[1]);
                Point point = new Point(pointX, pointY);
                mLandmarkPoints.add(point);
            }
        }
    }

    public int getLandmarkPointSize() {
        return mLandmarkPoints.size();
    }

    public Point getLandmarkPoint(int index) {
        return mLandmarkPoints.get(index);
    }
}
