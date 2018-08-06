package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.alibaba.whz.refresh_layout.State;

class RefreshView extends View {

    private Paint mPaint;

    // 拉动时候圆圈加载的百分比，0到1
    private float mPercent;
    // 状态，与RefreshLayout一致，用来控制绘制状态
    private State mState;
    private RectF oval;
    // 转菊花状态
    private int mNumType = 0;
    // 这个是动画完成的百分比，0到1
    private float mDegree;

    public int mHeight;
    public RefreshView(Context context){
        super(context);
        mState = State.RESET;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();
        mHeight = 180;
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mState = State.RESET;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();
        mHeight = 180;

    }

    public void reset(){
        this.mPercent = 0;
        this.mDegree = 0;
        this.mNumType  = 0;
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

    public void fail(){
        this.mNumType = 0;
        invalidate();
    }


    public void setmState(State state){
        this.mState = state;
    }


    private void setColorAndBig(int color,float big){
        mPaint.setColor(color);
        mPaint.setStrokeWidth(big);
    }

    @Override
    public void onMeasure(int with,int height){
        super.onMeasure(with,height);
    }


    @Override
    public void onDraw(Canvas canvas){
        float y = getHeight() * mPercent / 4.0f;
        float x = (getWidth() - y*2.0f) / 2.0f;

        /*
        * 这个if解决屏幕适配问题，解决高度特别特别大，宽度特别特别小的极端情况
        * 因为这是header的view，一般高度不会特别特别大，但还是以防万一
        * */
        if(x <= 0){
            x = 0;
            y = getHeight()*mPercent/2.0f - getWidth()/2.0f;
        }

        canvas.drawColor(0x00000000);

        switch (mState){
            case PULL:
            case PULLFULL:{
                mPaint.setStyle(Paint.Style.STROKE);
                setColorAndBig(Color.BLUE,3.0f);
                oval.set( x, y+(1-mPercent)*getHeight(),
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval,270,270*mPercent,false,mPaint);
                break;
            }
            case LOADING:{
                mPaint.setStyle(Paint.Style.STROKE);
                /*
                * 下面注释的代码，是第一次转菊花的动画实现，我觉得很有纪念意义，就没有删掉
                * */
/*                if(mNumType == 0)
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
                }*/
                oval.set(x, y,
                        getWidth() - x, getHeight() - y);
                /*
                * mNumType = 0的时候是转的第一圈，也就是蓝色圈有270度的时候，转完将nNumType设置为1,
                * mNumType = 1的时候转的圈分3部分，第一部分，蓝色圈出180度，第二部分，整个180度圆圈头移动到圈头，第三部分，180度圈尾部追回
                * */
                if(mNumType == 0)
                {
                    setColorAndBig(Color.BLUE,3.0f);
                    oval.set(x, y,
                            getWidth() - x, getHeight() - y);
                    canvas.drawArc(oval, 270+360*mDegree, 270-270*mDegree, false, mPaint);
                    if(mDegree == 1.0f){
                        mNumType = 1;
                    }
                }
                else {
                    if (mDegree < 0.33333333333333f) {
                        setColorAndBig(Color.BLUE,3.0f);
                        canvas.drawArc(oval, 270, 540 * mDegree, false, mPaint);
                    } else if (mDegree < 0.666666666666f) {
                        setColorAndBig(Color.BLUE,3.0f);
                        canvas.drawArc(oval, 270, 180, false, mPaint);
                        canvas.drawArc(oval, 90, 540 * mDegree - 180, false, mPaint);
                        setColorAndBig(Color.WHITE,4.0f);
                        canvas.drawArc(oval, 270, 540 * mDegree - 180, false, mPaint);
                    } else {
                        setColorAndBig(Color.BLUE,3.0f);
                        canvas.drawArc(oval, 90, 180, false, mPaint);
                        setColorAndBig(Color.WHITE,4.0f);
                        canvas.drawArc(oval, 90, 540 * mDegree - 360, false, mPaint);
                    }
                }
                
                break;
            }
            case COMPLETE:{
                /*
                * 绘制一个对号
                * */
                mPaint.setStyle(Paint.Style.FILL);
                setColorAndBig(Color.BLUE,3.0f);
                oval.set(x, y,
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval, 270, 360, true, mPaint);
                setColorAndBig(Color.WHITE,3.0f);
                canvas.drawCircle(x+y,2.5f*y,2.0f,mPaint);
                canvas.drawLine(x+y/2.0f,2.0f*y,x+y,2.5f*y,mPaint);
                canvas.drawLine(x+y,2.5f*y,x+1.5f*y,1.5f*y,mPaint);
                break;
            }
            case FAIL:{
                /*
                * 绘制一个错号
                * */
                mPaint.setStyle(Paint.Style.FILL);
                setColorAndBig(Color.BLUE,3.0f);
                oval.set(x, y,
                        getWidth() - x, getHeight() - y);
                canvas.drawArc(oval, 270, 360, true, mPaint);
                setColorAndBig(Color.WHITE,3.0f);
                canvas.drawLine(x+y/2.0f,1.5f*y,x+1.5f*y,2.5f*y,mPaint);
                canvas.drawLine(x+1.5f*y,1.5f*y,x+y/2.0f,2.5f*y,mPaint);
                break;
            }
        }
    }

}
