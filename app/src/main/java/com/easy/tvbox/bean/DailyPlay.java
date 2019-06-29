package com.easy.tvbox.bean;

import java.util.List;

public class DailyPlay {
    private long nowed;//当前日期  10 位
    private long fixed; //1560596400, 	//养生操固定播放时间
    private String cover;//"", 		//封面图
    private List<DailyRoll> roll;
    private List<DailyRoll> formal;
    private String type;

    public long getNowed() {
        return nowed;
    }

    public void setNowed(long nowed) {
        this.nowed = nowed;
    }

    public long getFixed() {
        return fixed;
    }

    public void setFixed(long fixed) {
        this.fixed = fixed;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<DailyRoll> getRoll() {
        return roll;
    }

    public void setRoll(List<DailyRoll> roll) {
        this.roll = roll;
    }

    public List<DailyRoll> getFormal() {
        return formal;
    }

    public void setFormal(List<DailyRoll> formal) {
        this.formal = formal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}