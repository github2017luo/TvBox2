package com.easy.tvbox.bean;

public class ErrorRespond<T> implements Respond<T> {
    private String body;
    private Error error = new Error();
    private T obj;//用于携带解析后的参数
    private boolean isOk = false;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    @Override
    public String getMessage() {
        if (error != null) {
            return error.getMessage();
        }
        return null;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
}
