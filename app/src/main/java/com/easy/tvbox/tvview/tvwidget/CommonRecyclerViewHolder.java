package com.easy.tvbox.tvview.tvwidget;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by owen on 16/2/24.
 */
public class CommonRecyclerViewHolder extends RecyclerView.ViewHolder {
    private CommonViewHolder mHolder;
    
    private CommonRecyclerViewHolder(CommonViewHolder mHolder) {
        super(mHolder.getConvertView());
        this.mHolder = mHolder;
    }
    
    public static CommonRecyclerViewHolder get(CommonViewHolder holder) {
        return new CommonRecyclerViewHolder(holder);
    }
    
    public static CommonRecyclerViewHolder get(Context context, ViewGroup parent, int layoutId){
        return new CommonRecyclerViewHolder(CommonViewHolder.get(context, parent, layoutId));
    }
    
    public CommonViewHolder getHolder(){
        return mHolder;
    }
}
