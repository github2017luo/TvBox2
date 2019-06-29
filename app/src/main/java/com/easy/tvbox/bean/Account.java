package com.easy.tvbox.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Account {
    @Id
    private long sqlId;//数据库ID
    private String name;
    private String id;
    private String shopName;
    private String phone;
    private String shopNo;

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getSqlId() {
        return sqlId;
    }

    public void setSqlId(long sqlId) {
        this.sqlId = sqlId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "sqlId=" + sqlId +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", shopName='" + shopName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
