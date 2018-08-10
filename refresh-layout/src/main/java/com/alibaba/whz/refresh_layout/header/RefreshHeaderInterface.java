package com.alibaba.whz.refresh_layout.header;

import android.view.View;
import android.view.ViewGroup;

public interface RefreshHeaderInterface {
    View sendHeaderView(ViewGroup parent);
    float sendHeaderHeightDp();
    float sendHeightDpWhenRefreshing();

    void reset();
    void pull(float pullPercent);
    void pullFull();
    void refreshing();
    void showRefreshResult(boolean refreshResult);
}
