package com.easy.tvbox.http;

public class NetworkChangeEvent {
    public boolean isConnected;

    public NetworkChangeEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
