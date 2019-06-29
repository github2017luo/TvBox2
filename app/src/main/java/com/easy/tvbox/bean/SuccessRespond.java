package com.easy.tvbox.bean;

public class SuccessRespond<T> implements Respond<T> {
    private String body;
    private T obj;//用于携带解析后的参数
    private int error;
    private String message;//成功的提示语
    private boolean isOk = true;

    @Override
    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public T getObj() {
        return obj;
    }

    @Override
    public void setObj(T obj) {
        this.obj = obj;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
