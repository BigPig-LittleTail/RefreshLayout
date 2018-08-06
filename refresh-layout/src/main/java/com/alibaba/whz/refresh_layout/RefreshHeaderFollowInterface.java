package com.alibaba.whz.refresh_layout;

public interface RefreshHeaderFollowInterface {
    void reset();
    void pull(float percent);
    void refreshing();
    void complete();
    void pullFull();
    void fail();
    boolean needPercent();
    int getHeaderHeight();
}
