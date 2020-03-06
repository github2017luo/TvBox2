package com.easy.tvbox.bean;

import android.text.TextUtils;

public class DailyItem {
    private String uid;
    private long idx;//视频播放顺序
    private String vid;//视频播放ID
    private String faceurl;//封面地址
    private String videourl;//视频地址
    private String audiourl;//音频播放地址
    private String title;
    private String duration;//视频时长 00:09:48
    private int type;
    private int period;
    private String date;//播放日期
    private String durationM;//视频时长 毫秒数
    private int position;//自己用的--第几个视频
    private int progress;//自己用 上次播放的位置

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getFaceurl() {
        return faceurl;
    }

    public void setFaceurl(String faceurl) {
        this.faceurl = faceurl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getAudiourl() {
        return audiourl;
    }

    public void setAudiourl(String audiourl) {
        this.audiourl = audiourl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDurationM() {
        return durationM;
    }

    public int getDurationMForLong() {
        return TextUtils.isEmpty(durationM) ? 0 : Integer.parseInt(durationM);
    }

    public void setDurationM(String durationM) {
        this.durationM = durationM;
    }

    @Override
    public String toString() {
        return "DailyItem{" +
                "position=" + position +
                ", progress=" + progress +
                '}';
    }
}

