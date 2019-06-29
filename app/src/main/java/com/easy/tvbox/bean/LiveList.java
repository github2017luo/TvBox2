package com.easy.tvbox.bean;

public class LiveList implements CBaseData{
//    {
//        uid: "d8f767f9cad242049ff0c53b6da06d97",
//                title: "《心身互动养生健康教育讲座》", 	//标题
//            attachmentId: "1560275579006_3.jpg", 	//封面图片文件名
//            coverUrl: "http://azitai.oss-cn-shanghai.aliyuncs.com/1560275579006_3.jpg?x-oss-process=style/photo_handle_320", 	//封面图片地址
//            fileId: "1560275585386_3(31).jpg", 	//海报图片，用于详情页面
//            posterUrl: "http://azitai.oss-cn-shanghai.aliyuncs.com/1560275585386_3(31).jpg?x-oss-process=style/photo_handle_320", 	//海报图片地址
//            beginDate: "2019-06-13 00:00:00", 	//开始时间
//            endDate: "2019-06-14 00:00:00"		//结束时间
//    }
    private String uid;
    private String title;
    private String attachmentId;
    private String coverUrl;
    private String fileId;
    private String posterUrl;
    private String beginDate;
    private String endDate;
    private int state;//0:结束 1：正在进行，2：未开始
    private String showTime;
    private long startTime;
    private long endTime;//--自己加的

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
