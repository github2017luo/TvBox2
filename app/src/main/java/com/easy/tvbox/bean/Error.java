package com.easy.tvbox.bean;

public class Error {
    private int code = 404;
    private String message = "数据异常";//错误的提示语

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "code=" + code + ", message=" + message;
    }
}
