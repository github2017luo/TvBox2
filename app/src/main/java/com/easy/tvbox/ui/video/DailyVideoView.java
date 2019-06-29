package com.easy.tvbox.ui.video;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.DailyPlay;
import com.easy.tvbox.bean.Respond;

public interface DailyVideoView extends BaseView {

    void dailyPlayUrlCallback(Respond<DailyPlay> respond);

    void playFormalVideo();
}
