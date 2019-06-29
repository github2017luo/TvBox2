package com.easy.tvbox.event;

public class DailyUpdateEvent {
    public int type;//0；请求 1：更新

    public DailyUpdateEvent(int type) {
        this.type = type;
    }
}
