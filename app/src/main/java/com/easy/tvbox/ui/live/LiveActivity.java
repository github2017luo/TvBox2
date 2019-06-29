package com.easy.tvbox.ui.live;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.databinding.LiveBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LiveActivity extends BaseActivity<LiveBinding> implements LiveView {

    @Inject
    LivePresenter presenter;
    LiveAdapter adapter;
    List<LiveList> liveLists = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.live;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void networkChange(boolean isConnect) {

    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(presenter);
    }

    @Override
    public void initView() {
        Account account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }

        adapter = new LiveAdapter(this, liveLists);
        mViewBinding.gridView.setAdapter(adapter);
    }
}
