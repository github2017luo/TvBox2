package com.easy.tvbox.ui.home;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.HomeMenu;

public class HomeMenuAdapter extends AbstractObjectPresenter<HomeMenuView> {

    public HomeMenuAdapter(Context context) {
        super(context);
    }

    @Override
    protected HomeMenuView onCreateView() {
        HomeMenuView homeMenuView = new HomeMenuView(getContext());
        return homeMenuView;

    }

    @Override
    public void onBindViewHolder(Object item, HomeMenuView homeMenuView) {
        if (item instanceof HomeMenu) {
            HomeMenu homeMenu = (HomeMenu) item;
            homeMenuView.updateUi(homeMenu);
        }
    }

}
