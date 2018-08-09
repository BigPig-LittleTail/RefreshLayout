package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.whz.refresh_layout.RefreshHeaderInterface;

public class RefreshHeaderNotView implements RefreshHeaderInterface{
    private Header header;
    public RefreshHeaderNotView(Context context){
        header = new Header(context);
    }

    public View sendHeaderView(ViewGroup parent){
        return header.sendHeaderView(parent);
    }
    public int sendHeaderHeight(){
        return header.sendHeaderHeight();
    }
    public int sendHeightWhenRefreshing(){
        return header.sendHeightWhenRefreshing();
    }

    public void reset(){
        header.reset();
    }
    public void pull(float x){
        header.pull(x);
    }
    public void pullFull(){
        header.pullFull();
    }
    public void refreshing(){
        header.refreshing();
    }
    public void complete(){
        header.complete();
    }
    public void fail(){
        header.fail();
    }
}
