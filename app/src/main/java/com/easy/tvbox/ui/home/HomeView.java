package com.easy.tvbox.ui.home;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.DailyData;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.Respond;

import java.util.List;

public interface HomeView extends BaseView {

    void carouselCallback(Respond<List<String>> respond);

    void queryForAudioCallback(Respond<DailyData> respond);

    void liveCallback(Respond<LiveData> respond);
}
