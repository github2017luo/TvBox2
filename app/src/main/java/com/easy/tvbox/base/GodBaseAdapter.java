package com.easy.tvbox.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.easy.tvbox.bean.CBaseData;

import java.util.List;

/**
 * Created by clarence on 2018/1/15.
 */

public abstract class GodBaseAdapter<T extends CBaseData> extends BaseAdapter {
    public Context context;
    protected List<T> dataList;
    protected LayoutInflater inflater;

    public GodBaseAdapter(Context context, List<T> data) {
        this.context = context;
        this.dataList = data;
        inflater = LayoutInflater.from(context);
    }

    public abstract void initItemView(View convertView,T itemData,int position);

    protected abstract int getItemLayout();

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public T getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(getItemLayout(), parent, false);
        }
        initItemView(convertView,dataList.get(position),position);
        return convertView;
    }
}
