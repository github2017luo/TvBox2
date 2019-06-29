package com.easy.tvbox.ui.music;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.GodBaseAdapter;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.MusicList;

import java.util.List;

public class MusicFragmentAdapter extends GodBaseAdapter<MusicList> {


    public MusicFragmentAdapter(Context context, List<MusicList> musicLists) {
        super(context, musicLists);
    }

    @Override
    public void initItemView(View convertView, MusicList itemData, int position) {

        ImageView ivPlayer = ViewHolder.getImageView(convertView, R.id.ivPlayer);
        changePlayerState(itemData, ivPlayer);

        String pic = "";
        List<String> pics = itemData.getPictures();
        if (pics != null && pics.size() > 0) {
            pic = pics.get(0);
        }
        Glide.with(context).load(pic).error(R.drawable.error_icon).placeholder(R.drawable.error_icon)
                .into(ViewHolder.getImageView(convertView, R.id.ivIcon));

        ViewHolder.getTextView(convertView, R.id.tvTitle).setText(itemData.getTitle());
        ViewHolder.getTextView(convertView, R.id.tvGeshou).setText(itemData.getGeshou());
        ViewHolder.getTextView(convertView, R.id.tvTime).setText(itemData.getDurationStr());
    }

    public void changePlayerState(MusicList itemData, ImageView ivPlayer) {
        if (itemData.getPlayerState() == 0) {
            ivPlayer.setImageResource(R.drawable.music_stop);
        } else if (itemData.getPlayerState() == 1) {
            ivPlayer.setImageResource(R.drawable.music_player);
        }
    }

    @Override
    protected int getItemLayout() {
        return R.layout.music_item;
    }
}
