package com.example.whz.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CoolRefreshHeader extends FrameLayout implements RefreshHeaderFollowInterface{
    private RefreshView refreshView;

    public CoolRefreshHeader(Context context) {
        this(context,null);
    }

    public CoolRefreshHeader(Context context, AttributeSet attr){
        super(context,attr);
        inflate(context,R.layout.activity_try,this);
        refreshView = findViewById(R.id.refreshView);
    }

    @Override
    public void pull(float percent){
        refreshView.setPercent(percent);
    }

}
