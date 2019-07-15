package com.easy.tvbox.ui.album;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.MusicData;

public interface AlbumListView extends BaseView {

    void queryMusicCallback(MusicData musicData);
}
