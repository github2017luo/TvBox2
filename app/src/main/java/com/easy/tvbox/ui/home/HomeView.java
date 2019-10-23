package com.easy.tvbox.ui.home;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.AppVersion;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;

import java.util.List;

public interface HomeView extends BaseView {

    void carouselCallback(Respond<List<String>> respond);

    void liveCallback(Respond<LiveData> respond);

    void countDownLive(LiveList liveList);

    void checkUpdateCallback(Respond<AppVersion> respond);

}
