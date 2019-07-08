package com.easy.tvbox.ui.album;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.Album;
import com.easy.tvbox.bean.Respond;

public interface AlbumView extends BaseView {

    void querySongSheetCallback(Respond<Album> respond);
}
