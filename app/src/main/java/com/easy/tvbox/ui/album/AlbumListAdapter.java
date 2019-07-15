package com.easy.tvbox.ui.album;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewAdapter;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewHolder;

import java.util.List;

public class AlbumListAdapter extends CommonRecyclerViewAdapter<MusicList> {

    public AlbumListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.album_list_item;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, MusicList itemData, int position) {

        ImageView ivPlayer = helper.getHolder().getView(R.id.ivPlayer);
        changePlayerState(itemData, ivPlayer);

        String pic = "";
        List<String> pics = itemData.getPictures();
        if (pics != null && pics.size() > 0) {
            pic = pics.get(0);
        }

        ImageView ivIcon = helper.getHolder().getView(R.id.ivIcon);
        Glide.with(mContext)
                .load(pic)
                .error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .into(ivIcon);

        helper.getHolder().setText(R.id.tvTitle, itemData.getTitle());
        helper.getHolder().setText(R.id.tvGeshou, itemData.getGeshou());
        helper.getHolder().setText(R.id.tvTime, itemData.getDurationStr());
    }

    public void changePlayerState(MusicList itemData, ImageView ivPlayer) {
        if (itemData.getPlayerState() == 0) {
            ivPlayer.setImageResource(R.drawable.music_stop);
        } else if (itemData.getPlayerState() == 1) {
            ivPlayer.setImageResource(R.drawable.music_player);
        }
    }
}
