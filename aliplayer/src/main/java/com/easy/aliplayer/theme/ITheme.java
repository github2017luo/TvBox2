package com.easy.aliplayer.theme;


import com.easy.aliplayer.view.AliyunVodPlayerView;
import com.easy.aliplayer.view.ControlView;
import com.easy.aliplayer.view.tipsview.ErrorView;
import com.easy.aliplayer.view.tipsview.NetChangeView;
import com.easy.aliplayer.view.tipsview.ReplayView;

/**
 * 主题的接口。用于变换UI的主题。
 * 实现类有{@link ErrorView}，{@link NetChangeView} , {@link ReplayView} ,{@link ControlView},
 * {GuideView} , {QualityView}, {SpeedView} , {TipsView},
 * {@link AliyunVodPlayerView}
 */

public interface ITheme {
    /**
     * 设置主题
     * @param theme 支持的主题
     */
    void setTheme(AliyunVodPlayerView.Theme theme);
}
