package com.example.whz.refreshlayout;

public interface RefreshHeaderInterface {
    void reset();
    void pull();
    void refreshing();
    void complete();
    void pullFull();
    void fail();
}