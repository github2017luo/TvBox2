
package com.easy.tvbox.ui.daily;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.DailyList;

public class DailyGridView extends BaseCardView {

    public DailyGridView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.daily_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(DailyList itemData) {
        View rootView = findViewById(R.id.rootView);
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

        TextView tvDownCount = findViewById(R.id.tvDownCount);
        if (TextUtils.isEmpty(itemData.getDownCount())) {
            tvDownCount.setVisibility(View.GONE);
        } else {
            tvDownCount.setVisibility(View.VISIBLE);
            tvDownCount.setText(" 倒计时：" + itemData.getDownCount());
        }
    }

    public void refreshCountDown(DailyList itemData) {
        TextView tvDownCount = findViewById(R.id.tvDownCount);
        if (TextUtils.isEmpty(itemData.getDownCount())) {
            tvDownCount.setVisibility(View.GONE);
        } else {
            tvDownCount.setVisibility(View.VISIBLE);
            tvDownCount.setText(" 倒计时：" + itemData.getDownCount());
        }
    }
}
