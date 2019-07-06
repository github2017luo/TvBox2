
package com.easy.tvbox.ui.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.MusicList;

import java.util.List;

public class MusicListView extends BaseCardView {

    public MusicListView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.music_item, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void updateUi(MusicList itemData) {

        ImageView ivPlayer = ViewHolder.getImageView(this, R.id.ivPlayer);
        changePlayerState(itemData, ivPlayer);

        String pic = "";
        List<String> pics = itemData.getPictures();
        if (pics != null && pics.size() > 0) {
            pic = pics.get(0);
        }
        Glide.with(getContext())
                .load(pic).error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .into(ViewHolder.getImageView(this, R.id.ivIcon));

        ViewHolder.getTextView(this, R.id.tvTitle).setText(itemData.getTitle());
        ViewHolder.getTextView(this, R.id.tvGeshou).setText(itemData.getGeshou());
        ViewHolder.getTextView(this, R.id.tvTime).setText(itemData.getDurationStr());
    }

    public void changePlayerState(MusicList itemData, ImageView ivPlayer) {
        if (itemData.getPlayerState() == 0) {
            ivPlayer.setImageResource(R.drawable.music_stop);
        } else if (itemData.getPlayerState() == 1) {
            ivPlayer.setImageResource(R.drawable.music_player);
        }
    }
}
