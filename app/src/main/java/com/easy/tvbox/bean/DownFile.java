package com.easy.tvbox.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DownFile {
    @Id
    private long sqlId;//数据库ID
    private String downLoadUrl;//下载完整路径，包括用户token
    private String downLoadPath;//下载路径，只有文件地址，不包括token(即问号后都不要)
    private String filePath;//文件目录
    private String fileName;//文件名
    private String path;// 完整路径
    int progress;//进度

    public String getDownLoadPath() {
        return downLoadPath;
    }

    public void setDownLoadPath(String downLoadPath) {
        this.downLoadPath = downLoadPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getProgress() {
        return progress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSqlId() {
        return sqlId;
    }

    public void setSqlId(long sqlId) {
        this.sqlId = sqlId;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DownFile{" +
                "downLoadUrl='" + downLoadUrl + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", progress=" + progress +
                '}';
    }
}
