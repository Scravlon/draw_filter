package com.scravlon.mobilevision1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class drawView extends View {
    private Path path = new Path();
    // setup initial color
    private int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;


    private ArrayList<Pair<Path,Paint>> paths_list;

    public drawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        paths_list = new ArrayList<>();
    }

    /**
     * Update color of the paint
     * @param c Color to be used
     */
    public void updateColor(int c){
        drawPaint.setColor(c);
    }

    public void updateSize(int paintSize){
        drawPaint.setStrokeWidth(5*paintSize);
    }

    /**
     * Setup the paint brush
     */
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStyle(Paint.Style.FILL); // change to fill
        drawPaint.setStyle(Paint.Style.STROKE); // change back to stroke
    }

    public void clonePaint(){
        Paint sth = new Paint();
        sth.setStrokeWidth(drawPaint.getStrokeWidth());
        sth.setAntiAlias(true);
        sth.setStrokeJoin(Paint.Join.ROUND);
        sth.setStrokeCap(Paint.Cap.ROUND);
        sth.setStyle(Paint.Style.STROKE);
        sth.setColor(drawPaint.getColor());
        drawPaint = sth;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        for(Pair<Path,Paint> p : paths_list){
            canvas.drawPath(p.first, p.second);
        }
    }

    public void undoPaint() {
//        if(!paths_list.isEmpty()) {
//            paths_list.remove(paths_list.size() - 1);
//        }
        paths_list.clear();
        postInvalidate();
    }
    // Get x and y and append them to the path
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                // Starts a new line in the path
                path.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Draws line between last point and this point
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }
        paths_list.add(Pair.create(path,drawPaint));
        postInvalidate(); // Indicate view should be redrawn
        return true; // Indicate we've consumed the touch
    }
}
