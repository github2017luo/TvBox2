package com.easy.tvbox.event;

/**
 * mqtt推送过来的消息内容
 */
public class MtMessage {
    private int idx;
    private String shopNo;
    private long timer;
    private String uid;
    private String vid;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    @Override
    public String toString() {
        return "MtMessage{" +
                "idx=" + idx +
                ", shopNo='" + shopNo + '\'' +
                ", timer=" + timer +
                ", uid='" + uid + '\'' +
                ", vid='" + vid + '\'' +
                '}';
    }
}
