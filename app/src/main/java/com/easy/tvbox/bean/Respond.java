package com.easy.tvbox.bean;

public interface Respond<T> {

    boolean isOk();

    String getBody();

    T getObj();

    void setObj(T obj);

    String getMessage();
}
