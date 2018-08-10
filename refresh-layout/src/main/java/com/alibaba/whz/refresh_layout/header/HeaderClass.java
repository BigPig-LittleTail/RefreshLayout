package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.alibaba.whz.refresh_layout.State;
import com.alibaba.whz.refresh_layout.header.progress.AbstractProgressView;
import com.alibaba.whz.refresh_layout.header.progress.ProgressView;

public class HeaderClass extends ViewGroup implements RefreshHeaderInterface{
    private final float mHeaderHeightDp;
    private final float mProgressHeightDp;
    private final int mWhenRefresingOneCircleTime;
    private AbstractProgressView mProgressView;
    private Animation mLoadingAnimation;


    private HeaderClass(Context context,Builder builder){
        super(context);
        this.mHeaderHeightDp = builder.mHeaderHeightDp;
        this.mProgressHeightDp = builder.mProgressHeightDp;
        this.mWhenRefresingOneCircleTime = builder.mWhenRefresingOneCircleTime;
        this.mProgressView = builder.mProgressView;
        addView(mProgressView);

        mProgressView.setPaintInfo(builder.mPaintColor,builder.mPaintWidth,builder.mWheatherProgressSolid);

        mLoadingAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mProgressView.setmDegree(interpolatedTime);
            }
        };
    }

    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float scale = getContext().getResources().getDisplayMetrics().density;
        float progressHeightPx = mProgressHeightDp * scale + 0.5f;
        mProgressView.measure(MeasureSpec.makeMeasureSpec((int)progressHeightPx,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)progressHeightPx,MeasureSpec.EXACTLY));

    }

    @Override
    public void onLayout(boolean changed,int l,int t,int r,int b){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        final int childHeight = height - getPaddingBottom();

        float scale = getContext().getResources().getDisplayMetrics().density;
        float progressHeightPx = mProgressHeightDp * scale + 0.5f;
        //mProgressView.layout(childLeft,childHeight - mProgressHeightDp,childWidth,childHeight);
        mProgressView.layout((int)((width - progressHeightPx)/ 2.0f),(int)(childHeight - progressHeightPx),(int)((width + progressHeightPx)/2.0f),childHeight);
    }


    @Override
    public float sendHeightDpWhenRefreshing(){
        return  mProgressHeightDp;
    }
    @Override
    public float sendHeaderHeightDp(){
        return mHeaderHeightDp;
    }


    @Override
    public View sendHeaderView(ViewGroup parent){
        return this;
    }

    @Override
    public void reset(){
        mProgressView.clearAnimation();
        mProgressView.setmState(State.RESET);
    }

    @Override
    public void pull(float percent){
        mProgressView.setmState(State.PULL);
        mProgressView.setmPercent(percent);
    }


    @Override
    public void pullFull(){
        mProgressView.setmState(State.PULLFULL);
        mProgressView.setmPercent(1.0f);
    }

    @Override
    public void refreshing(){
        mProgressView.setmState(State.LOADING);
        mLoadingAnimation.setDuration(this.mWhenRefresingOneCircleTime);
        mLoadingAnimation.setRepeatCount(Animation.INFINITE);
        mProgressView.startAnimation(mLoadingAnimation);
    }

    @Override
    public void showRefreshResult(boolean refreshResult){
        mProgressView.clearAnimation();
        if(refreshResult){
            mProgressView.setmState(State.COMPLETE);
        }
        else {
            mProgressView.setmState(State.FAIL);
        }
    }



    public static class Builder{
        private Context mContext;
        private float mHeaderHeightDp;
        private float mProgressHeightDp;
        private int mPaintColor;
        private float mPaintWidth;
        private boolean mWheatherProgressSolid;
        private int mWhenRefresingOneCircleTime;
        private AbstractProgressView mProgressView;

        public Builder(@NonNull Context context){
            this.mContext = context;
            mProgressView = new ProgressView(context);
            this.mHeaderHeightDp = 120;
            this.mProgressHeightDp = 60;
            this.mPaintColor = Color.BLUE;
            this.mPaintWidth = 3.0f;
            this.mWheatherProgressSolid = false;
            this.mWhenRefresingOneCircleTime = 1000;
        }

        public Builder setProgressView(AbstractProgressView progressView){
            this.mProgressView = progressView;
            return this;
        }

        public Builder setWheatherProgressSolid(boolean wheatherProgressSolid){
            this.mWheatherProgressSolid = wheatherProgressSolid;
            return this;
        }

        public Builder setmHeaderHeightDp(float mHeaderHeightDp){
            this.mHeaderHeightDp = mHeaderHeightDp;
            return this;
        }

        public Builder setmProgressHeightDp(float mProgressHeightDp){
            this.mProgressHeightDp = mProgressHeightDp;
            return this;
        }

        public Builder setmPaint(int mPaintColor,float mPaintWidth){
            this.mPaintColor = mPaintColor;
            this.mPaintWidth = mPaintWidth;
            return this;
        }

        public Builder setmWhenRefresingOneCircleTime(int mWhenRefresingOneCircleTime){
            this.mWhenRefresingOneCircleTime = mWhenRefresingOneCircleTime;
            return this;
        }


        public HeaderClass create(){
            return new HeaderClass(this.mContext,this);
        }
    }



}
