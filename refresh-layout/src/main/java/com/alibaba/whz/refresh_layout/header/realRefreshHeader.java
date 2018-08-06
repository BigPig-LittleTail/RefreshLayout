package com.alibaba.whz.refresh_layout.header;

import android.view.ViewGroup;

public abstract class Re extends ViewGroup {
    @Override
    public void onMeasure(int width,int height){
        super.onMeasure(width,height);
    }

    @Override
    public void onLayout(boolean changed,int l,int t,int r,int b){

    }

    public abstract void sss();

}
