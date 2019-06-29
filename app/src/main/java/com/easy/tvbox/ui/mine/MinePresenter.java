package com.easy.tvbox.ui.mine;

import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;

import javax.inject.Inject;

public class MinePresenter extends BasePresenter<MineView> {

    @Inject
    public MinePresenter() {

    }

    @Override
    public void onAttached() {

    }

    public Account getAccount() {
        return DataManager.getInstance().queryAccount();
    }
}
