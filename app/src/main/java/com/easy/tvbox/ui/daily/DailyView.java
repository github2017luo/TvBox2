package com.easy.tvbox.ui.daily;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.Daily;

import java.util.List;

public interface DailyView extends BaseView {

    void dailyCallback(List<Daily> daily);
}
