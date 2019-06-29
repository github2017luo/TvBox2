package com.easy.tvbox.bean;

import java.util.List;

public class DailyData {

//    totalElements: 6,
//    last: true,
//    totalPages: 1,
//    number: 0,
//    size: 20,
//    first: true,
//    numberOfElements: 6,


    private List<DailyList> content;
    private int totalElements;
    private boolean last;
    private int totalPages;
    private int number;
    private int size;
    private boolean first;
    private int numberOfElements;

    public List<DailyList> getContent() {
        return content;
    }

    public void setContent(List<DailyList> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
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

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
