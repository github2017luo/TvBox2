package com.easy.tvbox.ui.music;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.MusicData;

public interface MusicFragmentView extends BaseView {

    void queryMusicCallback(MusicData musicData,int videoId);
}
