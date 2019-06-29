package com.easy.tvbox.ui.video;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.Respond;

public interface VideoView extends BaseView {

    void livePlayUrlCallback(Respond<String> respond);
}
