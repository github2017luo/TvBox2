package com.easy.tvbox.ui.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.databinding.LauncherTvBinding;

import java.util.List;

import javax.inject.Inject;

public class LauncherActivity extends BaseActivity<LauncherTvBinding> implements LauncherView {

    @Inject
    LauncherPresenter testPresenter;
    boolean isLogin = true;

    @Override
    public int getLayoutId() {
        return R.layout.launcher_tv;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect && isLogin) {
            isLogin = false;
            mViewBinding.llContainer.postDelayed(() -> RouteManager.goLoginActivity(LauncherActivity.this), 3000);
        }
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(testPresenter);
    }

    @Override
    public void initView() {
        mViewBinding.llGoIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ComponentName componet = new ComponentName(getPackageName(), cls);
//                Intent intent = new Intent();
//                intent.setComponent(componet);
//                startActivity(intent);
                finish();

            }
        });
    }
}
