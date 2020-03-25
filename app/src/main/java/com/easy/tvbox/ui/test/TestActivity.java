package com.easy.tvbox.ui.test;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.databinding.TestBinding;
import com.easy.tvbox.event.StorageChangeEvent;
import com.easy.tvbox.receiver.UsbBroadcast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity<TestBinding> implements TestView {

    @Inject
    TestPresenter testPresenter;
    boolean isOk;

    @Override
    public int getLayoutId() {
        return R.layout.test;
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
        observerList.add(testPresenter);
    }

    @Override
    public void initView() {
        mViewBinding.testView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOk) {
                    UsbBroadcast.handlerPush(TestActivity.this);
                } else {
                    UsbBroadcast.handlerPull(TestActivity.this);
                }
                isOk = !isOk;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStorageChangeEvent(StorageChangeEvent event) {
        mViewBinding.testView.setText(event.msg);
    }
}
