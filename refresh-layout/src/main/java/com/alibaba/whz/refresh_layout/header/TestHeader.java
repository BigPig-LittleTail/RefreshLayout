package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.alibaba.whz.refresh_layout.RefreshHeaderFollowInterface;
import com.alibaba.whz.refresh_layout.State;

public class TestHeader  extends ViewGroup implements RefreshHeaderFollowInterface{

    private RefreshView mRefreshView;
    private Animation mLoadingAnimation;

    public TestHeader(Context context){
        super(context);
        mRefreshView = new RefreshView(context);
        addView(mRefreshView);

        mLoadingAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mRefreshView.refreshing(interpolatedTime);
            }
        };
    }

    public TestHeader(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mRefreshView = new RefreshView(context);
        addView(mRefreshView);

        mLoadingAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mRefreshView.refreshing(interpolatedTime);
            }
        };
    }

    public void onMeasure(int with,int height){
        super.onMeasure(with,height);


        mRefreshView.measure(MeasureSpec.makeMeasureSpec(with - getPaddingRight() - getPaddingLeft(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mRefreshView.mHeight,MeasureSpec.EXACTLY));
    }

    public void onLayout(boolean changed,int l,int t,int r,int b){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        final int childLeft = getPaddingLeft();
        final int childWidth = width  - getPaddingRight();
        final int childHeight = height - getPaddingBottom();
        mRefreshView.layout(childLeft,childHeight - mRefreshView.mHeight,childWidth,childHeight);
        Log.e("mRefreshView",""+mRefreshView);
    }

    @Override
    public void reset(){
        mRefreshView.setmState(State.RESET);
        mRefreshView.reset();
    }

    @Override
    public void pull(float percent){
        mRefreshView.setmState(State.PULL);
        mRefreshView.pull(percent);
    }

    @Override
    public void pullFull(){
        mRefreshView.setmState(State.PULLFULL);
        mRefreshView.pull(1.0f);
    }

    @Override
    public void refreshing(){
        mRefreshView.setmState(State.LOADING);
        mLoadingAnimation.setDuration(1000);
        mLoadingAnimation.setRepeatCount(Animation.INFINITE);
        mRefreshView.startAnimation(mLoadingAnimation);
    }

    @Override
    public void complete(){
        mRefreshView.clearAnimation();
        mRefreshView.setmState(State.COMPLETE);
        mRefreshView.complete();

    }

    @Override
    public void fail(){
        mRefreshView.clearAnimation();
        mRefreshView.setmState(State.FAIL);
        mRefreshView.fail();
    }


    @Override
    public boolean needPercent(){
        return true;
    }

    @Override
    public int getHeaderHeight(){
        return mRefreshView.mHeight;
    }

}
