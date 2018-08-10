package com.alibaba.whz.refresh_layout.header.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class ProgressView extends AbstractProgressView {
    public ProgressView(Context context){
        super(context);
    }

    public void drawWhenPull(Canvas canvas, Paint paint,final RectF oval, final float pullPercent){
        int wantPaintColor = paint.getColor();
        float wantPaintWidth = paint.getStrokeWidth();
        boolean useCenter = !(paint.getStyle() == Paint.Style.STROKE);
        //paint.setStyle(Paint.Style.STROKE);
        paint.setColor(wantPaintColor);
        paint.setStrokeWidth(wantPaintWidth);
        canvas.drawArc(oval,270,270*pullPercent,useCenter,paint);
    }

    public void drawWhenLoading(Canvas canvas, Paint paint, RectF oval,int circleNum,float degree) {
        int wantPaintColor = paint.getColor();
        float wantPaintWidth = paint.getStrokeWidth();
        boolean useCenter = !(paint.getStyle() == Paint.Style.STROKE);
        //paint.setStyle(Paint.Style.STROKE);

        paint.setColor(wantPaintColor);
        paint.setStrokeWidth(wantPaintWidth);

        if (circleNum == 0) {
            canvas.drawArc(oval, 270 + 360 * degree, 270 - 270 * degree, useCenter, paint);
        } else {
            if (degree < 0.33333333333333f) {
                canvas.drawArc(oval, 270, 540 * degree, useCenter, paint);
            } else if (degree < 0.666666666666f) {
                canvas.drawArc(oval, 270, 180, useCenter, paint);
                canvas.drawArc(oval, 90, 540 * degree - 180, useCenter, paint);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(wantPaintWidth+1.0f);
                canvas.drawArc(oval, 270, 540 * degree - 180, useCenter, paint);
            } else {
                canvas.drawArc(oval, 90, 180, useCenter, paint);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(wantPaintWidth+1.0f);
                canvas.drawArc(oval, 90, 540 * degree - 360, useCenter, paint);
            }
        }
        paint.setColor(wantPaintColor);
        paint.setStrokeWidth(wantPaintWidth);
    }

    public void drawResult(Canvas canvas,Paint paint,RectF oval,boolean refreshResult){
        int wantPaintColor = paint.getColor();
        float wantPaintWidth = paint.getStrokeWidth();
        Paint.Style style = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(wantPaintColor);
        paint.setStrokeWidth(wantPaintWidth);
        if(refreshResult){
            canvas.drawArc(oval, 270, 360, true, paint);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(wantPaintWidth+1.0f);
            canvas.drawCircle(oval.centerX(),oval.centerY()+oval.height()/4.0f,2.0f,paint);
            canvas.drawLine(oval.centerX() - oval.width()/4.0f,oval.centerY(),
                    oval.centerX(),oval.centerY()+oval.height()/4.0f,paint);
            canvas.drawLine(oval.centerX(),oval.centerY()+oval.height()/4.0f,
                    oval.centerX() + oval.width()/4.0f,oval.centerY() - oval.height()/4.0f,paint);
        }
        else{
            canvas.drawArc(oval, 270, 360, true, paint);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(wantPaintWidth+1.0f);
            canvas.drawCircle(oval.centerX(),oval.centerY(),2.0f,paint);
            canvas.drawLine(oval.centerX() - oval.width()/4.0f,oval.centerY() - oval.height()/4.0f,
                    oval.centerX() +oval.width()/4.0f,oval.centerY() + oval.height()/4.0f,paint);
            canvas.drawLine(oval.centerX() - oval.width()/4.0f,oval.centerY() + oval.height()/4.0f,
                    oval.centerX() +oval.width()/4.0f,oval.centerY() - oval.height()/4.0f,paint);
        }
        paint.setStyle(style);
        paint.setColor(wantPaintColor);
        paint.setStrokeWidth(wantPaintWidth);

    }


}
