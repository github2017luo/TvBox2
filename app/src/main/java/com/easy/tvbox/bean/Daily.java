package com.easy.tvbox.bean;

import java.util.List;

public class Daily {
    List<DailyItem> dailyItems;
    String imageUrl;

    public List<DailyItem> getDailyItems() {
        return dailyItems;
    }

    public void setDailyItems(List<DailyItem> dailyItems) {
        this.dailyItems = dailyItems;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
