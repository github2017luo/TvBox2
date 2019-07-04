package com.easy.tvbox.ui.live;

import android.content.Context;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.GodBaseAdapter;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.LiveList;

import java.util.List;

public class LiveAdapter extends GodBaseAdapter<LiveList> {


    public LiveAdapter(Context context, List<LiveList> musicLists) {
        super(context, musicLists);
    }

    @Override
    public void initItemView(View convertView, LiveList itemData, int position) {

        Glide.with(context).load(itemData.getPosterUrl()).error(R.drawable.error_icon).placeholder(R.drawable.error_icon)
                .into(ViewHolder.getImageView(convertView, R.id.ivIcon));

        ViewHolder.getTextView(convertView, R.id.tvTitle).setText(itemData.getTitle());
        ViewHolder.getTextView(convertView, R.id.tvTime).setText(itemData.getShowTime());

        View itemRoot = ViewHolder.getView(convertView, R.id.itemRoot);
        if (itemData.getState() == 0) {
            ViewHolder.getTextView(convertView, R.id.tvTip).setText("直播已结束");
            itemRoot.setEnabled(false);
        } else if (itemData.getState() == 1) {
            ViewHolder.getTextView(convertView, R.id.tvTip).setText("直播未开始");
            itemRoot.setEnabled(false);
        } else {
            ViewHolder.getTextView(convertView, R.id.tvTip).setText("正在直播");
            itemRoot.setEnabled(true);
            itemRoot.setOnClickListener(v -> RouteManager.goVideoActivity(context, itemData.getUid()));
        }
        itemRoot.setOnClickListener(v -> RouteManager.goVideoActivity(context, JSON.toJSONString(itemData)));
    }

    public void setDatas(List<LiveList> musicLists) {
        if (musicLists != null) {
            dataList.clear();
            dataList.addAll(musicLists);
            notifyDataSetChanged();
        }
    }

    @Override
    protected int getItemLayout() {
        return R.layout.live_item;
    }
}
