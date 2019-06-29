package com.easy.tvbox.bean;

import java.util.List;

public class MusicDetail {
//    {
//        uid: "09a585b5b28e441cba54b41117312510",   //主键
//                title: "《无病便是福》", 	//名称
//            attachmentId: "1518368181510_微信图片_2018021123142527.jpg@ajt@1560271871024_cd1.jpg", 	//封面图片文件名，以@ajt@隔开
//            musicId: "0bf5dcbeaae24b87b20e38767fb7c3e4", 		//音乐文件id
//            videoId: "0b4778c1fa814ee08d56f42f93112494", 		//视频文件id
//            lyric: "<p><img title=\"1518368244229060329.jpg\" alt=\"微信图片_2018021123142512.jpg\" src=\"/ueditor/upload/20180212/1518368244229060329.jpg\"/><br/></p>", 	//歌词，以富文本存储
//            geshou: "乌达木 演唱", 	//歌手
//            author: "词：纳贡毕力格    曲：热瓦迪", 	//作者
//            pictures: [
//        "http://azitai.oss-cn-shanghai.aliyuncs.com/1518368181510_微信图片_2018021123142527.jpg?x-oss-process=style/photo_handle_320",
//                "http://azitai.oss-cn-shanghai.aliyuncs.com/1560271871024_cd1.jpg?x-oss-process=style/photo_handle_320"
//                ]    //封面图片数组，可直接显示
//    }

    private String uid;
    private String title;
    private String attachmentId;
    private String musicId;
    private String videoId;
    private String lyric;
    private String geshou;
    private String author;
    private List<String> pictures;

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

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getGeshou() {
        return geshou;
    }

    public void setGeshou(String geshou) {
        this.geshou = geshou;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }
}
