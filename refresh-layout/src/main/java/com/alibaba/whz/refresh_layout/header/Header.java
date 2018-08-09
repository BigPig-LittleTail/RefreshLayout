package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.alibaba.whz.refresh_layout.RefreshHeaderInterface;
import com.alibaba.whz.refresh_layout.State;

public class Header extends ViewGroup implements RefreshHeaderInterface{

    private ProgressView mProgressView;
    private int mHeightWhenRefreshing;
    private Animation mLoadingAnimation;


    public Header(Context context){
        super(context);
        mProgressView = new ProgressView(context);
        mHeightWhenRefreshing = mProgressView.mHeight;
        addView(mProgressView);

        mLoadingAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mProgressView.refreshing(interpolatedTime);
            }
        };
    }

    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mProgressView.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec - getPaddingRight() - getPaddingLeft(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mHeightWhenRefreshing,MeasureSpec.EXACTLY));
    }

    @Override
    public void onLayout(boolean changed,int l,int t,int r,int b){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        final int childLeft = getPaddingLeft();
        final int childWidth = width  - getPaddingRight();
        final int childHeight = height - getPaddingBottom();
        mProgressView.layout(childLeft,childHeight - mHeightWhenRefreshing,childWidth,childHeight);
        Log.e("width","width"+width);
        Log.e("height","height"+height);
        Log.e("mProgressView","mProgressView"+mProgressView);
    }
    @Override
    public View sendHeaderView(ViewGroup parent){
        return this;
    }
    @Override
    public int sendHeightWhenRefreshing(){
        return  mHeightWhenRefreshing;
    }
    @Override
    public int sendHeaderHeight(){
        //return getMeasuredHeight();
        return mHeightWhenRefreshing * 2;
    }


    @Override
    public void reset(){
        mProgressView.setmState(State.RESET);
        mProgressView.reset();
    }

    @Override
    public void pull(float percent){
        mProgressView.setmState(State.PULL);
        mProgressView.pull(percent);
    }


    @Override
    public void pullFull(){
        mProgressView.setmState(State.PULLFULL);
        mProgressView.pull(1.0f);
    }

    @Override
    public void refreshing(){
        mProgressView.setmState(State.LOADING);
        mLoadingAnimation.setDuration(1000);
        mLoadingAnimation.setRepeatCount(Animation.INFINITE);
        mProgressView.startAnimation(mLoadingAnimation);
    }

    @Override
    public void complete(){
        mProgressView.clearAnimation();
        mProgressView.setmState(State.COMPLETE);
        mProgressView.complete();

    }

    @Override
    public void fail(){
        mProgressView.clearAnimation();
        mProgressView.setmState(State.FAIL);
        mProgressView.fail();
    }

}
