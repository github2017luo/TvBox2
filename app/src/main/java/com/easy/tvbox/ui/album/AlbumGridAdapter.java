package com.easy.tvbox.ui.album;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.AlbumList;

public class AlbumGridAdapter extends AbstractObjectPresenter<AlbumGridView> {

    public AlbumGridAdapter(Context context) {
        super(context);
    }

    @Override
    protected AlbumGridView onCreateView() {
        return new AlbumGridView(getContext());
    }

    @Override
    public void onBindViewHolder(Object item, AlbumGridView albumGridView) {
        if (item instanceof AlbumList) {
            AlbumList albumList = (AlbumList) item;
            albumGridView.updateUi(albumList);
        }
    }
}
