package com.easy.tvbox.bean;

public class DailyList implements CBaseData {
    //    {
//        uid: "558376fc1d2642c59a8aa7293516c93c",
//                title: "《心身互动养生健康教育讲座》",
//            attachmentId: "1560520778595_ljj2.jpg",
//            coverUrl: "http://azitai.oss-cn-shanghai.aliyuncs.com/1560520778595_ljj2.jpg?x-oss-process=style/photo_handle_320",
//            fileId: "1560520780801_cd3.jpg",
//            posterUrl: "http://azitai.oss-cn-shanghai.aliyuncs.com/1560520780801_cd3.jpg?x-oss-process=style/photo_handle_320",
//            liveFileId: "1560520783522_cd1.jpg",
//            liveCoverUrl: "http://azitai.oss-cn-shanghai.aliyuncs.com/1560520783522_cd1.jpg?x-oss-process=style/photo_handle_320",
//            fixedDate: "2019-06-15 19:00:00",
//            beginDate: "2019-06-15 18:30:00",
//            endDate: "2019-06-15 21:30:00",
//            language: "汉语",
//            type: "晚间课程"
//    }
    private String uid;
    private String title;
    private String attachmentId;
    private String coverUrl;
    private String fileId;
    private String posterUrl;
    private String liveFileId;
    private String liveCoverUrl;
    private String fixedDate;
    private String beginDate;
    private String endDate;
    private String language;
    private String type;
    private String showTime;
    private long startTime;
    private long endTime;//--自己加的
    private String downCount;//倒计时

    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDownCount() {
        return downCount;
    }

    public void setDownCount(String downCount) {
        this.downCount = downCount;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public String getLiveFileId() {
        return liveFileId;
    }

    public void setLiveFileId(String liveFileId) {
        this.liveFileId = liveFileId;
    }

    public String getLiveCoverUrl() {
        return liveCoverUrl;
    }

    public void setLiveCoverUrl(String liveCoverUrl) {
        this.liveCoverUrl = liveCoverUrl;
    }

    public String getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(String fixedDate) {
        this.fixedDate = fixedDate;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DailyList{" +
                "uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
