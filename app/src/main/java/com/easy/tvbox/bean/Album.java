package com.easy.tvbox.bean;

import java.util.List;

public class Album {
    List<AlbumList> content;
    private boolean last;
    private int totalPages;
    private int totalElements;

    private int number;
    private int size;

    public List<AlbumList> getContent() {
        return content;
    }

    public void setContent(List<AlbumList> content) {
        this.content = content;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
