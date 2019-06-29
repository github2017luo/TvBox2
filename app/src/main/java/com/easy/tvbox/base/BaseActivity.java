package com.easy.tvbox.base;

import android.content.Context;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.easy.tvbox.event.LogoutEvent;
import com.easy.tvbox.http.NetworkChangeEvent;
import com.easy.tvbox.http.NetworkConnectChangedReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<M extends ViewDataBinding> extends FragmentActivity {

    public Context mContext;
    public M mViewBinding;
    private List<BasePresenter> observerList = new ArrayList<>();
    private NetworkConnectChangedReceiver mNetWorkChangReceiver;/*网络状态变化的广播接收器*/
    protected boolean mNetConnected;/*网络连接的状态，true表示有网络，flase表示无网络连接*/
    public boolean isLoginActivity = false;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = this;
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            mViewBinding = DataBindingUtil.setContentView(this, layoutId);
        }
        initDagger();
        initPresenter();
        initView();

        //注册网络状态监听广播
        mNetWorkChangReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkChangReceiver, filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void initPresenter() {
        addPresenters(observerList);
        if (observerList != null && observerList.size() > 0) {
            for (BasePresenter lifecycleObserver : observerList) {
                lifecycleObserver.init(this);
                lifecycleObserver.setContext(this);
                getLifecycle().addObserver(lifecycleObserver);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyPresenter();
        unregisterReceiver(mNetWorkChangReceiver);
        EventBus.getDefault().unregister(this);
    }

    private void destroyPresenter() {
        if (observerList != null && observerList.size() > 0) {
            for (BasePresenter baseManager : observerList) {
                baseManager.onDetached();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        mNetConnected = event.isConnected;
        networkChange(event.isConnected);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        if (!isLoginActivity) {
            finish();
        }
    }

    public abstract void networkChange(boolean isConnect);

    public abstract void addPresenters(List<BasePresenter> observerList);

    public abstract void initView();

    public abstract int getLayoutId();

    public abstract void initDagger();

}
