package com.example.whz.refreshlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class RefreshView extends View {

    private Paint mPaint;// 绘图画笔

    private float mPercent;
    private State mState;
    private RectF oval;

    private int mNumType = 0;
    private float mDegree;




    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mState = State.RESET;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();


    }


    public void pull(float Percent){
        this.mPercent = Percent;
        invalidate();
    }

    public void refreshing(float degree){
        this.mDegree = degree;
        invalidate();

    }

    public void complete(){
        this.mNumType = 0;
        invalidate();
    }

    public void setmState(State state){
        this.mState = state;
    }

    public float getmDegree(){
        return  this.mDegree;
    }


    private void setColorAndBig(int color,float big){
        mPaint.setColor(color);
        mPaint.setStrokeWidth(big);
    }


    @Override
    public void onDraw(Canvas canvas){
        float y = getHeight() * mPercent / 4;
        float x = (getWidth() - y*2) / 2;
        canvas.drawColor(Color.WHITE);

        switch (mState){
            case PULL:{
                mPaint.setStyle(Paint.Style.STROKE);
                setColorAndBig(Color.BLUE,3.0f);
                oval.set( x, y+(1-mPercent)*getHeight(),
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval,270,360*mPercent,false,mPaint);
                break;
            }
            case PULLFULL:{
                mPaint.setStyle(Paint.Style.STROKE);
                setColorAndBig(Color.BLUE,3.0f);
                oval.set( x, y+(1-mPercent)*getHeight(),
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval,270,360*mPercent,false,mPaint);
                break;
            }
            case LOADING:{
                mPaint.setStyle(Paint.Style.STROKE);
                if(mNumType == 0)
                {
                    setColorAndBig(Color.BLUE,3.0f);
                    oval.set(x, y,
                            getWidth() - x, getHeight() - y);
                    canvas.drawArc(oval, 270, 360, false, mPaint);

                    setColorAndBig(Color.WHITE,4.0f);
                    canvas.drawArc(oval, 270, mDegree*360, false, mPaint);
                    if(mDegree == 1.0f){
                        mNumType = 1;
                    }
                }
                else{
                    setColorAndBig(Color.WHITE,4.0f);
                    oval.set(x, y,
                            getWidth() - x, getHeight() - y);
                    canvas.drawArc(oval, 270, 360, false, mPaint);

                    setColorAndBig(Color.BLUE,3.0f);
                    canvas.drawArc(oval, 270, mDegree*360, false, mPaint);
                    if(mDegree == 1.0f){
                        mNumType = 0;
                    }
                }
                break;
            }
            case COMPLETE:{
                mPaint.setStyle(Paint.Style.FILL);
                setColorAndBig(Color.BLUE,3.0f);
                canvas.drawArc(oval, 270, 360, true, mPaint);
                setColorAndBig(Color.WHITE,3.0f);
                canvas.drawCircle(x+y,2.5f*y,2.0f,mPaint);

                canvas.drawLine(x+y/2.0f,2.0f*y,x+y,2.5f*y,mPaint);
                canvas.drawLine(x+y,2.5f*y,x+1.5f*y,1.5f*y,mPaint);
            }
        }
    }

}
