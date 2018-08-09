package com.alibaba.whz.refresh_layout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface RefreshHeaderInterface {
    View sendHeaderView(ViewGroup parent);
    int sendHeaderHeight();
    int sendHeightWhenRefreshing();

    void reset();
    void pull(float x);
    void pullFull();
    void refreshing();
    void complete();
    void fail();
}
