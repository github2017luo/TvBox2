package com.easy.tvbox.ui.music;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.MusicList;

public class MusicListAdapter extends AbstractObjectPresenter<MusicListView> {

    public MusicListAdapter(Context context) {
        super(context);
    }

    @Override
    protected MusicListView onCreateView() {
        MusicListView musicListView = new MusicListView(getContext());
        return musicListView;
    }

    @Override
    public void onBindViewHolder(Object item, MusicListView homeMenuView) {
        if (item instanceof MusicList) {
            MusicList musicList = (MusicList) item;
            homeMenuView.updateUi(musicList);
        }
    }

}
