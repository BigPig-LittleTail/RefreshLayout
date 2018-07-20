package com.example.whz.refreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TabHost;


public class RefreshLayout extends ViewGroup{

    public final String TAG = "RefreshLayout";
    private View mHeader;
    private View mTarget;

    private Scroller mScroller;


    private float mPercent = 0.0f;
    private Animation mScaleAnimation;

    private int mCurrentOffsetTop;
    // 最小滑动距离
    private int mTouchSlop;
    private int mHeaderHeight;

    // 活动手指id
    private int mActivePointId = -1;
    // 最大下拉距离
    private float mTotalDragDistance = -1;
    // 手指按下的位置
    private float mInitDownY;
    private float mInitMotionY;
    // 保存的手指位置信息
    private float mSavedY;
    // mLastMove 和 mOffset用来解决多指触控偏移量问题
    // 已经偏移的偏移量
    private float mOffset1;
    private float mOffset2;
    private float mTotalOffset;


    // 刷新监控
    private RefreshLayout.OnRefreshListener mOnRefreshListener;
    // 子View是否能够滚动的回调
    private RefreshLayout.OnChildScrollUpCallback mChildScrollUpCallback;

    // 头部是否测量过
    private boolean mIsMeasureHeader;


    /* 这个变量是我看swiperefreshlayout源码有的一个变量，在onTouchEvent和onInterceptTouchEvent最先有个这样一个if
    *         if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
            }
    但我阅读其他部分，从来没有任何一个函数对mReturningToStart赋值过true，我所以我觉得这个if不会执行，就把他删了
    private boolean mReturningToStart;
    */

    // 是否正在刷新
    private boolean mRefreshing;
    // 是否正在下拉
    private boolean mIsBeingDragged;

    private boolean mIsSecondPointerDown;
    private boolean mIsSecondPointerMove;

    // 状态
    private State mState = State.RESET;

    private static final int INVALID_POINTER = -1;


    public void setOnRefreshListener(RefreshLayout.OnRefreshListener listener){
        this.mOnRefreshListener = listener;
    }


    /*设置刷新状态函数。
    * refreshing和mRefreshing实际上只有两个可能
    * true和false执行if，即本来不在刷新态，通过函数调用进入刷新态，直接改变状态到刷新态
    * false和true执行else，本来在刷新态，置为非刷新态，就进入刷新完成状态
    *
    * */
    public void setRefreshing(boolean refreshing,boolean refreshResult){
        if (refreshing && this.mRefreshing != refreshing) {
            this.mRefreshing = refreshing;
            changeState(State.LOADING);
            scrollTo(0,-(int)mTotalDragDistance);
        }
        else{
            if(this.mRefreshing != refreshing){
                this.enSureTarget();
                Log.e(TAG,"transrefreshing"+refreshing);
                this.mRefreshing = refreshing;
                if(mState == State.LOADING){
                    if(refreshResult){
                        changeState(State.COMPLETE);
                    }
                    else{
                        changeState(State.FAIL);
                    }
                    postDelayed(delayToScrollTopRunnable,2000);
                }
            }

        }

    }


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
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if(layoutParams == null) {
                layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(layoutParams);
            }
            mHeader = view;
            addView(mHeader);
        }
    }


    public RefreshLayout(Context context,AttributeSet attrs) {
        super(context,attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        RefreshHeader refreshHeader = new RefreshHeader(context);
        //CoolRefreshHeader refreshHeader = new CoolRefreshHeader(context);
        setHeader(refreshHeader);
        mRefreshing = false;
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
            mTotalDragDistance = mHeaderHeight;
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



    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
        Log.e(TAG,"onInterceptTouchEvent");
        int action = ev.getActionMasked();

//        if (this.mReturningToStart && action == 0) {
//            this.mReturningToStart = false;
//        }

        // 如果isEnabled为false，或者子view能滚动，或者正在刷新，不拦截touch事件
        Log.e(TAG,"canScrollUp"+canChildScrollUp());
        Log.e(TAG,"enabled"+this.isEnabled());
        Log.e(TAG,"refreshing"+mRefreshing);
        if (!this.isEnabled() /*|| this.mReturningToStart*/ || this.canChildScrollUp() || this.mRefreshing) {
            return false;
        }

        // 这个条件判断是因为我的refreshlayout不同于swiperefreshlayout，有一个complete状态
        // 并且complete到reset是在一个子线程异步执行的，所以要保证complete到reset之前，refreshlayout不处理事件
        if(mState == State.COMPLETE){
            return false;
        }

        int pointerIndex;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG,"I_ACTION_DOWN");
                mActivePointId = ev.getPointerId(0);
                mIsBeingDragged = false;
                mTotalOffset = 0;
                mOffset1 = 0;
                mOffset2 = 0;
                mIsSecondPointerMove = false;
                mIsSecondPointerDown = false;

                pointerIndex = ev.findPointerIndex(mActivePointId);

                mCurrentOffsetTop = mTarget.getTop();
                Log.e(TAG,"offset"+mCurrentOffsetTop);

                Log.e(TAG,"pointerIndex"+pointerIndex);
                if(pointerIndex < 0)
                    return false;
                mInitDownY = ev.getY(pointerIndex);

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"I_ACTION_MOVE");
                Log.e(TAG,"State"+mState);
                if(mActivePointId == INVALID_POINTER)
                    return false;
                pointerIndex = ev.findPointerIndex(mActivePointId);
                Log.e(TAG,"pointerIndex"+pointerIndex);

                if(pointerIndex < 0)
                    return false;
                float y = ev.getY(pointerIndex);
                startDragging(y);
                if (mIsBeingDragged){
                    onTouchEvent(ev);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.e(TAG,"I_ACTION_POINTER_UP");
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"I_ACTION_UP");
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointId = INVALID_POINTER;
                break;

        }
        return this.mIsBeingDragged;
    }

    private void startDragging(float y){
        final float yDiff = y - mInitDownY;
        // 滑动距离要大于最小滑动距离，否则拦截事件
        //Log.e(TAG,"yDiff"+yDiff);
        if(yDiff > mTouchSlop && !mIsBeingDragged && mState == State.RESET){
            mInitMotionY = mInitDownY + mTouchSlop;
            mIsBeingDragged = true;
            Log.e(TAG,"mInitMotionY"+mInitMotionY);


            changeState(State.PULL);


        }
    }

    private  void onSecondaryPointerUp(MotionEvent ev){
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if(pointerId == mActivePointId) {

            Log.e(TAG,Integer.toString(mActivePointId));
            final int newPointerIndex = pointerIndex == 0 ?1:0;
            mActivePointId = ev.getPointerId(newPointerIndex);

            Log.e(TAG,Integer.toString(mActivePointId));

            mTotalOffset -= mOffset1;

            mTotalOffset = mIsSecondPointerDown != mIsSecondPointerMove?mTotalOffset:mTotalOffset+mOffset2;


            mInitMotionY = mSavedY;
            Log.e(TAG,"mInitMotionY"+mInitMotionY);
        }
        mIsSecondPointerDown = mIsSecondPointerMove = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev){
        Log.e(TAG,"onTouchEvent");
        int action = ev.getActionMasked();

//        if (this.mReturningToStart && action == 0) {
//            this.mReturningToStart = false;
//        }

        if (!this.isEnabled() /*|| this.mReturningToStart*/ || this.canChildScrollUp() || this.mRefreshing) {
            return false;
        }

        int pointerIndex;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG,"ACTION_DOWN");
                mActivePointId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "ACTION_MOVE");
                pointerIndex = ev.findPointerIndex(mActivePointId);
                if (pointerIndex < 0)
                    return false;
                final float y = ev.getY(pointerIndex);

                mIsSecondPointerMove = mIsSecondPointerDown;
                startDragging(y);
                if (mIsBeingDragged) {
                    // 加上偏移量
                    final float overscrollTop = (y - mInitMotionY) + mTotalOffset;



                    if (overscrollTop > 0) {
                        if(mIsSecondPointerDown){
                            mOffset2 = (y- mInitMotionY);
                        }
                        else{
                            mOffset1 = (y- mInitMotionY);
                        }

                        moveSpinner(overscrollTop);
                    } else {
                        scrollTo(0,0);
                        changeState(State.RESET);
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.e(TAG, "ACTION_POINTER_DOWN");
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointId = ev.getPointerId(pointerIndex);

                mIsSecondPointerDown = true;

                // 上一个手指的初始点击位置保存
                mSavedY = mInitMotionY;
                mTotalOffset += mOffset1;
                // 获取新手指的点击位置
                pointerIndex = ev.findPointerIndex(mActivePointId);
                mInitDownY = mInitMotionY = ev.getY(pointerIndex);
                Log.e(TAG,Float.toString(mInitMotionY));

                Log.e(TAG,"mInitMotionY"+mInitMotionY);

                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                Log.e(TAG,"ACTION_POINTER_UP");
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "ACTION_UP");
                pointerIndex = ev.findPointerIndex(mActivePointId);
                if (pointerIndex < 0)
                    return false;
                final float y = ev.getY(pointerIndex);
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;


                    final float overscrollTop = (y - mInitMotionY) + mTotalOffset;

                    //scrollTo(0,-(int)overscrollTop);
                    if(overscrollTop > 0)
                        finishSpinner(overscrollTop);

                }
                mActivePointId = INVALID_POINTER;
                mTotalOffset = 0;
                mOffset1 = 0;
                mOffset2 = 0;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;

        }
        return true;
    }

    /*
    *  moveSpinner和finishSpinner可以看为状态机
    *  RESET状态到PULL的触发条件是ACTION_MOVE
    *  PULL到RESET状态的触发条件是手指up（偏移量不够）
    *  PULL状态到PULLFULL的触发条件是偏移量达到最大值
    *  PULLFULL状态到PULL状态的触发条件是手指上划偏移量减少
    *  PULLFULL状态到LOADING状态的触发条件是手指up
    *  LOADING到COMPLETE的触发条件是回调Onrefresh
    * */
    private void moveSpinner(float overscrollTop){
        //Log.e(TAG,Float.toString(overscrollTop));

        switch (mState) {
            case RESET:
                changeState(State.PULL);
                break;
            case PULL:
                if(overscrollTop < mTotalDragDistance) {
                    mPercent = overscrollTop / mTotalDragDistance;
                    changeState(mState);
                    scrollTo(0,-(int)overscrollTop);
                    Log.e(TAG,Integer.toString(getScrollY()));
                }
                else{
                    scrollTo(0,-(int)mTotalDragDistance);
                    changeState(State.PULLFULL);
                }
                break;
            case PULLFULL:
                if(overscrollTop < mTotalDragDistance) {
                    scrollTo(0,-(int)overscrollTop);
                    changeState(State.PULL);
                }
                else{
                    scrollTo(0,-(int)mTotalDragDistance);

                }
                break;
        }

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {

            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            if (mScroller.getCurrX() == getScrollX()
                    && mScroller.getCurrY() == getScrollY() ) {
                //postInvalidate();
                invalidate();
            }
        }
    }


    private void finishSpinner(float overscrollTop){
        switch (mState){
            case PULL:
                mScroller.startScroll(0,getScrollY(),0,-getScrollY());
                invalidate();
                changeState(State.RESET);
                break;
            case PULLFULL:
                changeState(State.LOADING);
                mRefreshing = true;
                mOnRefreshListener.onRefresh();
                break;
        }
    }

    private void changeState(State state) {
        boolean nowIsPull = state != this.mState;
        this.mState = state;

        RefreshHeaderInterface refreshHeader = this.mHeader instanceof RefreshHeaderInterface ? ((RefreshHeaderInterface) this.mHeader) : null;

        if (refreshHeader != null) {
            switch (state) {
                case RESET:
                    refreshHeader.reset();
                    break;
                case PULL:
                    if(nowIsPull){
                        refreshHeader.pull();
                    }
                    break;
                case PULLFULL:
                    refreshHeader.pullFull();
                    break;
                case LOADING:
                    refreshHeader.refreshing();
                    break;
                case COMPLETE:
                    refreshHeader.complete();
                    break;
                case FAIL:
                    refreshHeader.fail();
                    break;
            }
        }

        RefreshHeaderFollowInterface x = this.mHeader instanceof RefreshHeaderFollowInterface ?((RefreshHeaderFollowInterface) this.mHeader):null;
        if(x != null){
            switch (state) {
                case RESET:
                    break;
                case PULL:
                    x.pull(mPercent);
                    break;
                case PULLFULL:
                    x.pullfull();
                    break;
                case LOADING:
                    x.refreshing();
                    break;
                case COMPLETE:
                    x.complete();
                    break;
                case FAIL:
                    x.fail();
            }
        }

    }



    private Runnable delayToScrollTopRunnable = new Runnable() {
        @Override
        public void run() {
           changeState(State.RESET);

           mScroller.startScroll(0,getScrollY(),0,-getScrollY());
           invalidate();

        }
    };



    public boolean canChildScrollUp() {
        if (this.mChildScrollUpCallback != null) {
            return this.mChildScrollUpCallback.canChildScrollUp(this, this.mTarget);
        } else {
            return this.mTarget instanceof ListView ? ListViewCompat.canScrollList((ListView)this.mTarget, -1) : this.mTarget.canScrollVertically(-1);
        }
    }

    // 子view可以告知refreshlayout它是否可以刷新

    public void setOnChildScrollUpCallback(@Nullable RefreshLayout.OnChildScrollUpCallback callback) {
        this.mChildScrollUpCallback = callback;
    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(@NonNull RefreshLayout var1, @Nullable View var2);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
