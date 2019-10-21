
package com.easy.tvbox.ui.daily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.utils.DimensUtils;

public class DailyGridView extends BaseCardView {
    int width, height;

    public DailyGridView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.daily_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        int[] screens = DimensUtils.getWidthHeight(context);
        width = (screens[0] - DimensUtils.dp2px(context, 120)) / 2;
        height = width / 2 + DimensUtils.dp2px(context, 60);
    }

    public void updateUi(Daily daily) {
        View rootView = findViewById(R.id.rootView);
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        rootView.setLayoutParams(layoutParams);

        ImageView ivIcon = findViewById(R.id.ivIcon);
        Glide.with(getContext())
                .load(daily.getImageUrl())
                .error(R.drawable.error_icon)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.error_icon)
                .into(ivIcon);
    }
}
