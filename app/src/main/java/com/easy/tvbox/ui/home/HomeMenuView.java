
package com.easy.tvbox.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.easy.tvbox.R;
import com.easy.tvbox.bean.HomeMenu;

public class HomeMenuView extends BaseCardView {

    public HomeMenuView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.home_menu, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(HomeMenu homeMenu) {
        ImageView ivIcon = findViewById(R.id.ivIcon);
        ivIcon.setImageResource(homeMenu.getIconResourceId());
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(homeMenu.getTitle());
        View llContain = findViewById(R.id.llContain);
        llContain.setBackgroundResource(homeMenu.getBgResourceId());
        ViewGroup.LayoutParams layoutParams = llContain.getLayoutParams();
        layoutParams.height = homeMenu.getItemHeight();
        layoutParams.width = homeMenu.getItemWidth();
        llContain.setLayoutParams(layoutParams);
    }
}
