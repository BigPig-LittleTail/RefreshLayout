package com.alibaba.whz.refresh_layout.header.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.alibaba.whz.refresh_layout.State;

public abstract class AbstractProgressView extends View {

    private Paint mPaint;
    private RectF mOval;
    private float mPercent;
    private float mDegree;
    private State mState;
    private int mCircleNum;

    public AbstractProgressView(Context context){
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOval = new RectF();
        mPercent = 0;
        mDegree = 0;
        mCircleNum = 0;
        mState = State.RESET;
    }

    public void setPaintInfo(int mWantPaintColor,float mWantPaintWidth,boolean WheatherProgressSolid){
       mPaint.setColor(mWantPaintColor);
       mPaint.setStrokeWidth(mWantPaintWidth);
       if(WheatherProgressSolid)
           mPaint.setStyle(Paint.Style.FILL);
       else
           mPaint.setStyle(Paint.Style.STROKE);
    }


    public void setmDegree(float mDegree){
        if(this.mDegree != mDegree && mDegree == 1.0f)
            mCircleNum ++;
        this.mDegree = mDegree;
        invalidate();
    }

    public void setmPercent(float mPercent){
        this.mPercent = mPercent;
        invalidate();

    }

    public void setmState(State state){
        this.mState = state;
        switch (mState){
            case RESET:
                this.mPercent = 0;
                this.mDegree = 0;
                this.mCircleNum  = 0;
                break;
            case COMPLETE:
            case FAIL:
                this.mCircleNum = 0;
                invalidate();
                break;
        }
    }



    public abstract void drawWhenPull(Canvas canvas,Paint paint,RectF mOval,float pullPercent);

    public abstract void drawWhenLoading(Canvas canvas,Paint paint,RectF mOval,int circleNum,float degree);

    public abstract void drawResult(Canvas canvas,Paint paint,RectF mOval,boolean refreshResult);


    public void onDraw(Canvas canvas){
        float y = getHeight() * mPercent / 4.0f;
        float x = (getWidth() - y*2.0f) / 2.0f;

        if(x <= 0){
            x = 0;
            y = getHeight()*mPercent/2.0f - getWidth()/2.0f;
        }

        canvas.drawColor(0x00000000);
        switch (mState){
            case PULL:
            case PULLFULL:
                mOval.set( x, y+(1-mPercent)*getHeight(),getWidth() - x, getHeight() - y);
                drawWhenPull(canvas,mPaint,mOval,mPercent);
                break;
            case LOADING:
                mOval.set(x, y, getWidth() - x, getHeight() - y);
                drawWhenLoading(canvas,mPaint,mOval,mCircleNum,mDegree);
                break;
            case COMPLETE:
                mOval.set(x, y, getWidth() - x, getHeight() - y);
                drawResult(canvas,mPaint,mOval,true);
                break;
            case FAIL:
                mOval.set(x, y, getWidth() - x, getHeight() - y);
                drawResult(canvas,mPaint,mOval,false);
                break;
        }
    }

}
