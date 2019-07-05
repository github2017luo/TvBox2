package com.easy.tvbox.ui.home;

import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.easy.tvbox.R;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.HomeMenu;
import com.easy.tvbox.event.LiveUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class HomeMenuFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRowAdapter();
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
        gridPresenter.setNumberOfColumns(4);
        gridPresenter.setShadowEnabled(true);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new HomeMenuAdapter(getActivity()));
        setAdapter(mAdapter);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof HomeMenu) {
                HomeMenu homeMenu = (HomeMenu) item;
                switch (homeMenu.getId()) {
                    case 0:
                        RouteManager.goLiveActivity(getContext());
                        EventBus.getDefault().post(new LiveUpdateEvent(0));
                        break;
                    case 1:
                        RouteManager.goDailyActivity(getContext());
                        break;
                    case 2:
                        RouteManager.goMusicActivity(getContext());
                        break;
                    case 3:
                        RouteManager.goMineActivity(getContext());
                        break;
                }
            }
        });

        prepareEntranceTransition();

        List<HomeMenu> homeMenus = new ArrayList<>();
        HomeMenu live = new HomeMenu("大课直播", R.drawable.icon_live2, R.drawable.home_module_bg_1);
        live.setId(0);
        homeMenus.add(live);
        HomeMenu daily = new HomeMenu("每日课程", R.drawable.icon_course2, R.drawable.home_module_bg_2);
        daily.setId(1);
        homeMenus.add(daily);
        HomeMenu music = new HomeMenu("音  乐", R.drawable.icon_music2, R.drawable.home_module_bg_3);
        music.setId(2);
        homeMenus.add(music);
        HomeMenu mine = new HomeMenu("我  的", R.drawable.icon_my2, R.drawable.home_module_bg_4);
        mine.setId(3);
        homeMenus.add(mine);

        mAdapter.addAll(0, homeMenus);

        startEntranceTransition();

    }
}
