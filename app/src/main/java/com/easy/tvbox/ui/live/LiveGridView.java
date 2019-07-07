
package com.easy.tvbox.ui.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.LiveList;

public class LiveGridView extends BaseCardView {

    public LiveGridView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.live_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(LiveList itemData) {
        View rootView =findViewById(R.id.rootView);
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.height = itemData.getHeight();
        layoutParams.width = itemData.getWidth();
        rootView.setLayoutParams(layoutParams);

        ImageView ivIcon = findViewById(R.id.ivIcon);
        Glide.with(getContext())
                .load(itemData.getPosterUrl())
                .error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .into(ivIcon);

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(itemData.getTitle());
        TextView tvTime = findViewById(R.id.tvTime);
        tvTime.setText(itemData.getShowTime());

        if (itemData.getState() == 0) {
            TextView tvTip = findViewById(R.id.tvTip);
            tvTip.setText("直播已结束");
        } else if (itemData.getState() == 1) {
            TextView tvTip = findViewById(R.id.tvTip);
            tvTip.setText("直播未开始");
        } else {
            TextView tvTip = findViewById(R.id.tvTip);
            tvTip.setText("正在直播");
        }
    }
}
