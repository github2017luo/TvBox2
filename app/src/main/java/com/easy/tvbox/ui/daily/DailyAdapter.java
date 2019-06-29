package com.easy.tvbox.ui.daily;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.GodBaseAdapter;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.DailyList;

import java.util.List;

public class DailyAdapter extends GodBaseAdapter<DailyList> {


    public DailyAdapter(Context context, List<DailyList> musicLists) {
        super(context, musicLists);
    }

    @Override
    public void initItemView(View convertView, DailyList itemData, int position) {

        Glide.with(context).load(itemData.getPosterUrl()).error(R.drawable.error_icon).placeholder(R.drawable.error_icon)
                .into(ViewHolder.getImageView(convertView, R.id.ivIcon));

        ViewHolder.getTextView(convertView, R.id.tvTitle).setText(itemData.getTitle());
        ViewHolder.getTextView(convertView, R.id.tvTime).setText(itemData.getShowTime());

        TextView tvDownCount = ViewHolder.getTextView(convertView, R.id.tvDownCount);
        if (TextUtils.isEmpty(itemData.getDownCount())) {
            tvDownCount.setVisibility(View.GONE);
        } else {
            tvDownCount.setVisibility(View.VISIBLE);
            tvDownCount.setText(" 倒计时：" + itemData.getDownCount());
        }
    }

    @Override
    protected int getItemLayout() {
        return R.layout.daily_item;
    }
}
