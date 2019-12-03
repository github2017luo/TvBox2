package com.easy.tvbox.ui.daily;

import android.util.TypedValue;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.databinding.DailyBinding;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;

import java.util.List;

import javax.inject.Inject;

public class DailyActivity extends BaseActivity<DailyBinding> implements DailyView {

    @Inject
    DailyPresenter presenter;
    List<Daily> daily;
    FocusBorder mFocusBorder;

    @Override
    public int getLayoutId() {
        return R.layout.daily;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
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
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);

        //1034:395
        //x:200
        mViewBinding.ivHan.setOnClickListener(v -> {
            if (daily != null && daily.size() > 0 && daily.get(0) != null) {
                RouteManager.goDailyVideoActivity(DailyActivity.this, JSON.toJSONString(daily.get(0)));
            } else {
                ToastUtils.showShort("暂无数据");
            }
        });
        mViewBinding.ivHan.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.3f));

        mViewBinding.ivMeng.setOnClickListener(v -> {
            if (daily != null && daily.size() > 1 && daily.get(1) != null) {
                RouteManager.goDailyVideoActivity(DailyActivity.this, JSON.toJSONString(daily.get(1)));
            } else {
                ToastUtils.showShort("暂无数据");
            }
        });
        mViewBinding.ivMeng.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.3f));

        mFocusBorder.setVisible(true);
        onMoveFocusBorder(mViewBinding.ivHan, 1.3f);
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }
    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            presenter.queryDaily();
        }
    }

    public void setNoData() {
        mViewBinding.loadingView.setStatus(LoadingView.STATUS_NODATA);
    }

    @Override
    public void dailyCallback(List<Daily> daily) {
        this.daily = daily;
        mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
//        if (daily != null && daily.size() > 0) {
//            if (dailyGridFragment != null) {
//                dailyGridFragment.updateData(daily);
//            }
//        }
    }
}
