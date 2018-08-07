package com.alibaba.whz.refresh_layout;

import android.content.Context;
import android.view.View;

public interface RefreshHeaderInterface {
    View sendHeaderView(Context context);
    int sendHeaderHeight();
    int sendHeightWhenRefreshing();

    boolean sendWeatherNeedPercent();

    void reset();
    void pull(float x);
    void pullFull();
    void refreshing();
    void complete();
    void fail();
}
