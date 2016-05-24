/*
 * Copyright 2016 Tzutalin
 *
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
 */

package com.tzutalin.dlibtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.tzutalin.dlib.VisionDetRet;

import java.util.List;

public class RecognitionScoreView extends View {
    private static final float TEXT_SIZE_DIP = 24;
    private static final String TAG = "RecognitionScoreView";
    private List<VisionDetRet> results;
    private final float textSizePx;
    private final Paint fgPaint;
    private final Paint bgPaint;
    private final Paint landmarkPaint;

    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint();
        fgPaint.setTextSize(textSizePx);

        bgPaint = new Paint();
        bgPaint.setColor(0xcc4285f4);

        landmarkPaint = new Paint();
        landmarkPaint.setColor(Color.GREEN);
        landmarkPaint.setStrokeWidth(2);
        landmarkPaint.setStyle(Paint.Style.STROKE);
    }

    public void setResults(final List<VisionDetRet> results) {
        this.results = results;
        postInvalidate();
    }

    @Override
    public void onDraw(final Canvas canvas) {
        final int x = 10;
        int y = (int) (fgPaint.getTextSize() * 1.5f);

        canvas.drawPaint(bgPaint);

        if (results != null) {
            for (final VisionDetRet ret : results) {
                // TODO
                /*canvas.drawText(recog.getTitle() + ": " + recog.getConfidence(), x, y, fgPaint);
                y += fgPaint.getTextSize() * 1.5f;*/

                float resizeRatio = 1.0f;
                Rect bounds = new Rect();
                bounds.left = (int) (ret.getLeft() * resizeRatio);
                bounds.top = (int) (ret.getTop() * resizeRatio);
                bounds.right = (int) (ret.getRight() * resizeRatio);
                bounds.bottom = (int) (ret.getBottom() * resizeRatio);

                canvas.drawRect(bounds, landmarkPaint);

                String label = ret.getLabel();
                Log.d(TAG, "draw label: " + label);
                // Draw face landmarks if exists.The format looks like face_landmarks 1,1:50,50,:...
                if (label.startsWith("face_landmarks ")) {
                    String[] landmarkStrs = label.replaceFirst("face_landmarks ", "").split(":");
                    for (String landmarkStr : landmarkStrs) {
                        String[] xyStrs = landmarkStr.split(",");
                        int pointX = Integer.parseInt(xyStrs[0]);
                        int pointY = Integer.parseInt(xyStrs[1]);
                        pointX = (int) (pointX * resizeRatio);
                        pointY = (int) (pointY * resizeRatio);

                        Log.d(TAG, String.format("draw (%d, %d)", pointX, pointY));
                        canvas.drawCircle(pointX, pointY, 2, landmarkPaint);
                    }
                }
            }
        }
    }
}
