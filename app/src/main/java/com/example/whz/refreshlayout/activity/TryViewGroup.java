package com.example.whz.refreshlayout.activity;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.alibaba.whz.refresh_layout.header.RefreshHeader;

public class TryViewGroup  extends ViewGroup implements NestedScrollingParent{

    Scroller mScroller;
    private View mHeader;
    private View mTarget;
    private int mHeaderHeight;
    private boolean mIsMeasureHeader;
    private int mCurrentOffsetTop;

    private void enSureTarget(){
        if(mTarget == null) {
            for(int i = 0;i<getChildCount();i++) {
                View child = this.getChildAt(i);
                if(!child.equals( mHeader)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    public void setHeader(View view){
        if(view != null && view != mHeader){
            removeView(mHeader);
            LayoutParams layoutParams = view.getLayoutParams();
            if(layoutParams == null) {
                layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(layoutParams);
            }
            mHeader = view;
            addView(mHeader);
        }
    }

    public TryViewGroup(Context context, AttributeSet attr){
        super(context,attr);
        RefreshHeader refreshHeader = new RefreshHeader(context);
        setHeader(refreshHeader);
        mScroller= new Scroller(getContext());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        if(mTarget == null)
            enSureTarget();
        if(mTarget == null)
            return;

        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),MeasureSpec.EXACTLY));
        measureChild(mHeader,widthMeasureSpec,heightMeasureSpec);

        if(!mIsMeasureHeader){
            mIsMeasureHeader = true;
            mHeaderHeight = mHeader.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if(getChildCount() == 0){
            return;
        }

        if(mTarget == null)
            enSureTarget();
        if(mTarget == null)
            return;

        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        int headerWidth = mHeader.getMeasuredWidth();
        mHeader.layout((width/2 - headerWidth/2),-mHeaderHeight + mCurrentOffsetTop,(width/2 + headerWidth/2),mCurrentOffsetTop);
    }




}
