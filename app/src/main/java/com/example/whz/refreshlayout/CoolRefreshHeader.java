package com.example.whz.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class CoolRefreshHeader extends FrameLayout implements RefreshHeaderFollowInterface{
    private RefreshView mRefreshView;
    private Animation mLoadingAnimation;

    public CoolRefreshHeader(Context context) {
        this(context,null);
    }

    public CoolRefreshHeader(Context context, AttributeSet attr){
        super(context,attr);
        inflate(context,R.layout.activity_try,this);
        mRefreshView = findViewById(R.id.refreshView);

        // 用animation驱动RefreshView的绘制
        mLoadingAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mRefreshView.refreshing(interpolatedTime);
            }
        };
    }

    @Override
    public void pull(float percent){
        mRefreshView.setmState(State.PULL);
        mRefreshView.pull(percent);
    }

    @Override
    public void pullfull(){
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

}
