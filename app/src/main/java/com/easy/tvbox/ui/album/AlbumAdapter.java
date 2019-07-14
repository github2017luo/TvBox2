package com.easy.tvbox.ui.album;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.bean.AlbumList;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewAdapter;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewHolder;

public class AlbumAdapter extends CommonRecyclerViewAdapter<AlbumList> {


    public AlbumAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.album_item;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, AlbumList albumList, int position) {

        View llContain = helper.getHolder().getView(R.id.llContain);
        ViewGroup.LayoutParams layoutParams = llContain.getLayoutParams();
        layoutParams.height = albumList.getHeight();
        layoutParams.width = albumList.getWidth();
        llContain.setLayoutParams(layoutParams);

        ImageView ivIcon = helper.getHolder().getView(R.id.ivIcon);
        Glide.with(mContext)
                .load(albumList.getImage())
                .error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .into(ivIcon);

        helper.getHolder().setText(R.id.tvTitle, albumList.getName());
        helper.getHolder().setText(R.id.tvNum, albumList.getTotal());
    }
}
