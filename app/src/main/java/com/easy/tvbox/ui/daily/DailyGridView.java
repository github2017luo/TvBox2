
package com.easy.tvbox.ui.daily;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.Daily;

public class DailyGridView extends BaseCardView {

    public DailyGridView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.daily_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(Daily daily) {
        ImageView ivIcon = findViewById(R.id.ivIcon);
        Glide.with(getContext())
                .load(daily.getImageUrl())
                .error(R.drawable.error_icon)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.error_icon)
                .into(ivIcon);
    }
}
