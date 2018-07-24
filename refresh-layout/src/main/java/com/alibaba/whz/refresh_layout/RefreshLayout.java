package com.alibaba.whz.refresh_layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

import com.alibaba.whz.refresh_layout.header.CoolRefreshHeader;


public class RefreshLayout extends ViewGroup{

    public static final String TAG = "RefreshLayout";
    private static final int INVALID_POINTER = -1;
    private View mHeader;
    private View mTarget;

    private Scroller mScroller;

    // 头部下拉程度，百分比
    private float mPercent = 0.0f;

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
    // 已经偏移的偏移量
    private float mTotalOffset;
    // 每个手指的滑动信息
    private SparseArray<PointerInformation> allPointersInformation;

    // 刷新监控
    private RefreshLayout.OnRefreshListener mOnRefreshListener;
    // 子View是否能够滚动的回调
    private RefreshLayout.OnChildScrollUpCallback mChildScrollUpCallback;

    // 头部是否测量过
    private boolean mIsMeasureHeader;

    private boolean mWhetherHeaderNeedPercent;


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
    // 状态
    private State mState = State.RESET;

    private class PointerInformation{
        float mPointerOffset;
        float mInitMotionY;
        boolean mHasMoved;
        private PointerInformation(float mPointerOffset,float mInitMotionY,boolean mHasMoved){
            this.mPointerOffset = mPointerOffset;
            this.mInitMotionY = mInitMotionY;
            this.mHasMoved = mHasMoved;
        }
    }


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
            LayoutParams layoutParams = view.getLayoutParams();
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

        // 在这里可以该头部类型，也可以用setHeader在外部更换
        //RefreshHeader refreshHeader = new RefreshHeader(context);
        CoolRefreshHeader refreshHeader = new CoolRefreshHeader(context);
        setHeader(refreshHeader);
        mRefreshing = false;
        mWhetherHeaderNeedPercent = true;
        mScroller= new Scroller(getContext());
        allPointersInformation = new SparseArray<>();
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

                pointerIndex = ev.findPointerIndex(mActivePointId);

                mCurrentOffsetTop = mTarget.getTop();
                if(pointerIndex < 0)
                    return false;

                mInitDownY = ev.getY(pointerIndex);

                allPointersInformation.put(mActivePointId,new PointerInformation(0,mInitDownY,false));

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"I_ACTION_MOVE");
                if(mActivePointId == INVALID_POINTER)
                    return false;
                pointerIndex = ev.findPointerIndex(mActivePointId);

                if(pointerIndex < 0)
                    return false;
                float y = ev.getY(pointerIndex);

                Log.e(TAG,"mActivePointId"+mActivePointId);
                startDragging(y);
                // 新建当前活动手指的信息，存在allPointersInformation中

                if (mIsBeingDragged){
                    onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e(TAG,"ACTION_POINTER_DOWN");
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointId = ev.getPointerId(pointerIndex);
                mInitDownY = ev.getY(pointerIndex);

                allPointersInformation.put(mActivePointId,new PointerInformation(0,mInitDownY,false));
                Log.e(TAG,"pointer_Y"+ev.getY(pointerIndex));
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
                allPointersInformation.clear();
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
            allPointersInformation.get(mActivePointId).mInitMotionY = mInitMotionY;

            mIsBeingDragged = true;
            changeState(State.PULL);
        }
    }


    private  void onSecondaryPointerUp(MotionEvent ev){
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        PointerInformation upPointerInfo = allPointersInformation.get(pointerId);
        if(pointerId == mActivePointId) {

            final int newPointerIndex = pointerIndex == 0 ?1:0;
            mActivePointId = ev.getPointerId(newPointerIndex);

            Log.e(TAG,"mActivePointId" + mActivePointId);

            PointerInformation nowActivePointerInfo = allPointersInformation.get(mActivePointId);

            // 总偏移减去当前活跃手指曾经作出的偏移
            mTotalOffset = mTotalOffset - nowActivePointerInfo.mPointerOffset;
            // 总偏移加上抬起手指作出的偏移
            mTotalOffset = upPointerInfo.mHasMoved ? mTotalOffset+upPointerInfo.mPointerOffset:mTotalOffset;
            // 还原当前活跃手指的InitMotionY
            mInitMotionY = nowActivePointerInfo.mInitMotionY;
            Log.e(TAG,"mInitMotionY"+mInitMotionY);
        }
        // 移除抬起手指的信息
        allPointersInformation.delete(pointerId);
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
                Log.e(TAG,"mActivePointId"+mActivePointId);
                if (pointerIndex < 0)
                    return false;
                final float y = ev.getY(pointerIndex);
                // 获取当前活跃手指的信息
                PointerInformation activePointerInfo = allPointersInformation.get(mActivePointId);

                // 表示该手指曾经作出过偏移，手指信息中的偏移量是有用值，这个为了避免有的手指，点了一下而没有移动
                // 造成的偏移量计算错误
                activePointerInfo.mHasMoved = true;


                //startDragging(y);
                if (mIsBeingDragged) {
                    // 加上偏移量
                    final float overscrollTop = (y - mInitMotionY) + mTotalOffset;
                    activePointerInfo.mPointerOffset = (y- mInitMotionY);
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop);
                    } else {
                        // 这个避免向上快速滑动，留黑边
                        scrollTo(0,0);
                        changeState(State.RESET);
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                // 获取当前活跃手指的信息
                Log.e(TAG,"ACTION_POINTER_DOWN");
                PointerInformation nowActivePointerInfo = allPointersInformation.get(mActivePointId);
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointId = ev.getPointerId(pointerIndex);

                // 总偏移量加上当前活跃手指的偏移量
                mTotalOffset += nowActivePointerInfo.mPointerOffset;
                // 获取新手指的点击位置
                pointerIndex = ev.findPointerIndex(mActivePointId);
                mInitDownY = mInitMotionY = ev.getY(pointerIndex);

                // 新建手指信息保存到allPointerInformation
                allPointersInformation.put(mActivePointId,new PointerInformation(0,mInitMotionY,false));
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
                allPointersInformation.clear();
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    final float overscrollTop = (y - mInitMotionY) + mTotalOffset;
                    if(overscrollTop > 0)
                        finishSpinner();
                }
                mActivePointId = INVALID_POINTER;
                mTotalOffset = 0;
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
                    if(mWhetherHeaderNeedPercent)
                        changeState(mState);
                    scrollTo(0,-(int)overscrollTop);
                }
                else{
                    // 为了防止快速滑动，状态改变滞后造成的留黑边
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
                invalidate();
            }
        }
    }


    private void finishSpinner(){
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
        this.mState = state;


        RefreshHeaderFollowInterface refreshHeader = this.mHeader instanceof RefreshHeaderFollowInterface ?((RefreshHeaderFollowInterface) this.mHeader):null;
        if(refreshHeader != null){
            mWhetherHeaderNeedPercent = refreshHeader.needPercent();
            switch (state) {
                case RESET:
                    refreshHeader.reset();
                    break;
                case PULL:
                    refreshHeader.pull(mPercent);
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
