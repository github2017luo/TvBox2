package com.easy.tvbox.bean;

public class AlbumList {

//    uid: "0f7ebeeddb904f3d8a7c8e343635e896",
//    created: "2019-07-07 22:58:01",
//    updated: "2019-07-07 22:58:01",
//    shopNo: "S0001",
//    name: "我的歌单2", 	//歌单名称
//    image: "http://azitai.oss-cn-shanghai.aliyuncs.com/1562511478072_1.jpg?x-oss-process=style/photo_handle_320", 	//歌单图片
//    status: "1"

    private String uid;
    private String created;
    private String updated;
    private String shopNo;
    private String image;
    private String name;
    private String status;
    private String num;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
