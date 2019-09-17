/*
 * Copyright (C) The Android Open Source Project
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
package com.scravlon.mobilevision1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.scravlon.mobilevision1.camera.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private Context context;
    private MainActivity mainCon;


    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;


    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay, Context context, MainActivity mainActivity) {
        super(overlay);
        this.context = context;
        this.mainCon = mainActivity;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
//       Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.index);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(face.getEulerZ());
//        matrix.postSkew(face.getEulerY()/90,face.getEulerZ()/90);
//        Bitmap must = Bitmap.createScaledBitmap(icon,(int)face.getWidth(),(int)face.getHeight()/2,true);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(must, 0, 0, must.getWidth(), must.getHeight(), matrix, true);
        float nosex = 0.0f;
        float nosey = 0.0f;
        PointF mouth = null;
        PointF eyel = null;
        PointF eyer= null;


        for(Landmark l : face.getLandmarks()){
            if(l.getType() == Landmark.NOSE_BASE && mainCon.noseBit != null){
                PointF p = face.getPosition();
                nosex = translateX(l.getPosition().x);
                nosey = translateY(l.getPosition().y );
                canvas.drawBitmap(mainCon.noseBit,p.x-nosex,p.y-nosey,null);
               // canvas.drawBitmap(rotatedBitmap,nosex,nosey,null);

            } else if(l.getType() == Landmark.LEFT_EYE){
                eyel = l.getPosition();
            } else if(l.getType() == Landmark.RIGHT_EYE){
                eyer = l.getPosition();
            } else if(l.getType() == Landmark.BOTTOM_MOUTH){
                mouth = l.getPosition();
            }
        }
        //Head
        if(mainCon.headBit != null){
            //TODO Triangle
            float ftx = eyel.x+face.getPosition().x;
            float fty = eyer.y-face.getPosition().y/2;

            //canvas.drawBitmap(headBit,translateX(ftx),translateY(fty),null);
            Bitmap clone = Bitmap.createScaledBitmap(mainCon.headBit,(int)face.getWidth()*2,(int)face.getHeight()*2,true);
            canvas.drawBitmap(clone,translateX(ftx),translateY(fty),null);
        }
        if(mainCon.mustBit != null){
            //TODO Triangle
            float mx = mouth.x;
            float my = mouth.y;
            //canvas.drawBitmap(mustBit,translateX(mx),translateX(my),null);
            Bitmap clone = Bitmap.createScaledBitmap(mainCon.mustBit,(int)face.getWidth()*2,(int)face.getHeight()*2,true);
            canvas.drawBitmap(clone,translateX(face.getPosition().x),translateY(face.getPosition().y),null);
        }



    }
}
