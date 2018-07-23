package com.example.whz.refreshlayout.header;

public interface RefreshHeaderFollowInterface {
    void reset();
    void pull(float percent);
    void refreshing();
    void complete();
    void pullFull();
    void fail();

    boolean needPercent();
}
