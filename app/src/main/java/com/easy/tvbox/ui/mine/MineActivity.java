package com.easy.tvbox.ui.mine;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.MineBinding;
import com.easy.tvbox.event.LogoutEvent;
import com.easy.tvbox.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public class MineActivity extends BaseActivity<MineBinding> implements MineView {

    @Inject
    MinePresenter minePresenter;
    Account account;

    @Override
    public int getLayoutId() {
        return R.layout.mine;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(minePresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAccountInfo();
    }

    @Override
    public void initView() {
        mViewBinding.tvUpdatePhone.setOnClickListener(v -> RouteManager.goUpdatePhoneActivity(MineActivity.this));

        mViewBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutFragmentDialog dialog = new LogoutFragmentDialog();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "logout");
            }
        });
    }

    private void refreshAccountInfo() {
        account = minePresenter.getAccount();
        if (account == null) {
            finish();
        }
        mViewBinding.tvNo.setText("编号: " + account.getId());
        mViewBinding.tvPhone.setText("手机号: " + account.getPhone());
        mViewBinding.tvHospital.setText("归属医院: " + account.getShopName());
        mViewBinding.tvIp.setText("网络信息: " + CommonUtils.getIp(this));
    }

    @Override
    public void networkChange(boolean isConnect) {

    }

    public void logout() {
        DataManager.getInstance().deleteAccount();
        RouteManager.goLoginActivity(this);
        EventBus.getDefault().post(new LogoutEvent());
    }
}