package com.easy.tvbox.bean;

import android.text.TextUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PlayProgress {
    @Id
    private long sqlId;//数据库ID
    private String id;
    private String progress;

    public long getSqlId() {
        return sqlId;
    }

    public void setSqlId(long sqlId) {
        this.sqlId = sqlId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public int getProgressInt() {
        return TextUtils.isEmpty(progress) ? 0 : Integer.parseInt(progress);
    }
}
