package com.easy.tvbox.ui.mine;

import android.util.TypedValue;
import android.view.View;

import com.easy.tvbox.BuildConfig;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.MineBinding;
import com.easy.tvbox.event.LogoutEvent;
import com.easy.tvbox.utils.SystemUtils;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public class MineActivity extends BaseActivity<MineBinding> implements MineView {

    @Inject
    MinePresenter minePresenter;
    Account account;
    FocusBorder mFocusBorder;

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

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    @Override
    public void initView() {
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.touming)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .shadowColorRes(R.color.touming)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .noShimmer()
                .build(this);

        mViewBinding.tvUpdatePhone.setOnClickListener(v -> RouteManager.goUpdatePhoneActivity(MineActivity.this));
        mViewBinding.tvUpdatePhone.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.logout.setOnClickListener(v -> {
            LogoutFragmentDialog dialog = new LogoutFragmentDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "logout");
        });
        mViewBinding.logout.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.tvFileManager.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        mViewBinding.tvFileManager.setOnClickListener(v -> {
            String packageName = "com.droidlogic.FileBrower";
            boolean hasIn = SystemUtils.isAppExist(MineActivity.this, packageName);
            if (hasIn) {
                SystemUtils.openApp(MineActivity.this, packageName);
            } else {
                ToastUtils.showShort("应用还未安装，请先安装应用");
            }
        });

        mFocusBorder.setVisible(true);
        onMoveFocusBorder(mViewBinding.tvUpdatePhone, 1.1f);
    }

    private void refreshAccountInfo() {
        account = minePresenter.getAccount();
        if (account == null) {
            finish();
        }
        mViewBinding.tvNo.setText("编号: " + account.getId());
        mViewBinding.tvPhone.setText("手机号: " + account.getPhone());
        mViewBinding.tvHospital.setText("归属医院: " + account.getShopName());
        mViewBinding.tvIp.setText("当前版本: " + BuildConfig.VERSION_NAME);
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
