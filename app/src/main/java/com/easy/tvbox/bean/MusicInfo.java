package com.easy.tvbox.bean;

public class MusicInfo {

//    fileName: "09.ebedqin ugei jirgal saihan ( 字幕 ).mp4", 	//文件名
//    size: 26532425, 		//文件大小
//    duration: 215.6467, 	//时长
//    playUrl: "http://sp.ajitai.com.cn/0b4778c1fa814ee08d56f42f93112494/41ad1a9b6c3d45de8adbc4d8d8ab8389-5287d2089db37e62345123a1be272f8b.mp4?auth_key=1560192187-1ce49967385f4688b3a377234c39ad1a-0-c3540cac1bbac12d6c4948ba56e7ce2a", 		//播放地址，地址有效时间2天
//    coverUrl: "http://sp.ajitai.com.cn/snapshot/0b4778c1fa814ee08d56f42f9311249400005.jpg?auth_key=1560192187-3123cdeaf42a4feda17945e839e2d586-0-c264c1c4935acec6f201c4b6ceba2de6"	//封面截图
    private String fileName;
    private double size;
    private double duration;
    private String playUrl;
    private String coverUrl;
    private String uid;//自己添加的

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "fileName='" + fileName + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", playUrl='" + playUrl + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
