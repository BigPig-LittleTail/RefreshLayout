package com.example.whz.refreshlayout;

public interface RefreshHeaderFollowInterface {
    void pull(float percent);
    void refreshing();
    void complete();
    void pullfull();
}
