package com.easy.tvbox.bean;

import java.util.List;

public class LiveData {
//    totalElements: 2,
//    totalPages: 1,
//    last: true,
//    number: 0,
//    size: 10,
//    numberOfElements: 2,
//    first: true
    private int totalElements;
    private int totalPages;
    private boolean last;
    private int number;
    private int size;
    private int numberOfElements;
    private boolean first;
    private List<LiveList> content;
    private LiveSort sort;

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
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

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public List<LiveList> getContent() {
        return content;
    }

    public void setContent(List<LiveList> content) {
        this.content = content;
    }

    public LiveSort getSort() {
        return sort;
    }

    public void setSort(LiveSort sort) {
        this.sort = sort;
    }
}
