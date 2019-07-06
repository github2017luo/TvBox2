package com.easy.tvbox.bean;

public class HomeMenu {
    public int id;
    public int iconResourceId;
    public int bgResourceId;
    public String title;
    public int itemHeight;
    public int itemWidth;

    public HomeMenu(String title, int iconResourceId, int bgResourceId) {
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.bgResourceId = bgResourceId;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBgResourceId() {
        return bgResourceId;
    }

    public void setBgResourceId(int bgResourceId) {
        this.bgResourceId = bgResourceId;
    }
}
