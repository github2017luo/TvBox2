package com.easy.tvbox.bean;

import java.util.List;

public class Daily {
    List<DailyItem> dailyItems;
    int imageResource;

    public List<DailyItem> getDailyItems() {
        return dailyItems;
    }

    public void setDailyItems(List<DailyItem> dailyItems) {
        this.dailyItems = dailyItems;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
