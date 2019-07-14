
package com.easy.tvbox.ui.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.AlbumList;
import com.easy.tvbox.bean.HomeMenu;

public class AlbumGridView extends BaseCardView {

    public AlbumGridView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.album_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(AlbumList albumList) {


    }
}
