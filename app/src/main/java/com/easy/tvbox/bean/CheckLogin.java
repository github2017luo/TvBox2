package com.easy.tvbox.bean;

public class CheckLogin {
//    create_time: "2019-10-10 18:59:02",  //有效时间
//    aid: "13959932888", 	//登录账号，如用户未扫描，会返回异常信息
//    key: "09ab2c2e39044f5c9b8499e8d59c50d3",   //二维码的唯一值
//    expires_time: "2019-10-10 19:00:02"   //失效时间

    private String create_time;
    private String aid;
    private String key;
    private String expires_time;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpires_time() {
        return expires_time;
    }

    public void setExpires_time(String expires_time) {
        this.expires_time = expires_time;
    }
}
