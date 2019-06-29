package com.easy.tvbox.event;

public class LiveUpdateEvent {
    public int type;//0；请求 1：更新

    public LiveUpdateEvent(int type) {
        this.type = type;
    }
}
