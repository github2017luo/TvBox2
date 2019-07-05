/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.easy.tvbox.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.easy.tvbox.R;
import com.easy.tvbox.bean.HomeMenu;

public class HomeMenuView extends BaseCardView {

    public HomeMenuView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.home_menu, this);
//        setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                ImageView mainImage = findViewById(R.id.main_image);
//                View container = findViewById(R.id.container);
//                if (hasFocus) {
//                    container.setBackgroundResource(R.drawable.character_focused);
//                    mainImage.setBackgroundResource(R.drawable.character_focused);
//                } else {
//                    container.setBackgroundResource(R.drawable.character_not_focused_padding);
//                    mainImage.setBackgroundResource(R.drawable.character_not_focused);
//                }
//            }
//        });
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(HomeMenu homeMenu) {
        ImageView ivIcon = findViewById(R.id.ivIcon);
        ivIcon.setImageResource(homeMenu.getIconResourceId());
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(homeMenu.getTitle());
        LinearLayout llContain = findViewById(R.id.llContain);
        llContain.setBackgroundResource(homeMenu.getBgResourceId());
    }
}
