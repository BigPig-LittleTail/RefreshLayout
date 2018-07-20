package com.example.whz.refreshlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyView  extends View {
    private Paint mPaint;// 绘图画笔

    private RectF oval;
    private int state;

    private float degree = 0;
    private int type = 0;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        state = 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth((float) 3.0);
        mPaint.setStyle(Paint.Style.STROKE);


    }

    public void reDraw(int state,float degree){
        this.state = state;
        this.degree = degree;
        invalidate();
    }

    public float getDegree(){
        return this.degree;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float y = getHeight() / 4;
        float x = (getWidth() - y * 2) / 2;
        canvas.drawColor(Color.WHITE);

        Log.e("XXX","onDraw");
        if(state == 0)
        {
            oval.set(x, y,
                    getWidth() - x, getHeight() - y);
            canvas.drawArc(oval, 270, 360, false, mPaint);

        }
        else if(state == 1)
        {
            if(type == 0)
            {
                mPaint.setColor(Color.BLUE);
                mPaint.setStrokeWidth((float) 3.0);
                oval.set(x, y,
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval, 270, 360, false, mPaint);

                mPaint.setStrokeWidth((float) 4.0);
                mPaint.setColor(Color.WHITE);

                canvas.drawArc(oval, 270, degree*360, false, mPaint);
                if(degree == 1.0f){
                    type = 1;
                }
            }
            else{
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth((float) 4.0);

                oval.set(x, y,
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval, 270, 360, false, mPaint);

                mPaint.setColor(Color.BLUE);
                mPaint.setStrokeWidth((float) 3.0);
                canvas.drawArc(oval, 270, degree*360, false, mPaint);
                if(degree == 1.0f){
                    type = 0;
                }
            }

        }
        else {
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth((float) 3.0);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth()/2,getHeight()/2,Math.min(getHeight()/4,getWidth()/4),mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawLine(getWidth() * 3/8,getHeight()/2,getWidth()/2,getHeight()*5/8,mPaint);
        }
    }

}

