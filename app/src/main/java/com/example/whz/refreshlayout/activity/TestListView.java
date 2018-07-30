package com.example.whz.refreshlayout.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class TestListView extends ListView{
    public TestListView(Context context, AttributeSet attr){
        super(context,attr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        Log.e("testListView","dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e("testListView","onTouchEvent");
        return super.onTouchEvent(event);
    }
}

