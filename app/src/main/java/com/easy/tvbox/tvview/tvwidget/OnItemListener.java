package com.easy.tvbox.tvview.tvwidget;

import android.view.View;
import android.view.ViewGroup;


public interface OnItemListener {
    void onItemSelected(ViewGroup parent, View itemView, int position);
    void onItemClick(ViewGroup parent, View itemView, int position);
}
