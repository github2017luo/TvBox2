package com.easy.tvbox.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleRegistry;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragment<M extends ViewDataBinding> extends Fragment {

    public M mViewBinding;
    public Context mContext;
    View view;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    private List<BasePresenter> observerList = new ArrayList<>();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        initDagger();
        initPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        view = mViewBinding.getRoot();
        initView(view);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initPresenter() {
        addPresenters(observerList);
        if (observerList != null && observerList.size() > 0) {
            for (BasePresenter lifecycleObserver : observerList) {
                lifecycleObserver.init(mRegistry);
                lifecycleObserver.setContext(getContext());
                getLifecycle().addObserver(lifecycleObserver);
            }
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }


    public abstract int getLayoutId();

    public abstract void initView(View view);

    public abstract void addPresenters(List<BasePresenter> observerList);

    public abstract void initDagger();

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyPresenter();
    }

    private void destroyPresenter() {
        if (observerList != null && observerList.size() > 0) {
            for (BasePresenter godBasePresenter : observerList) {
                godBasePresenter.onDetached();
            }
        }
    }
}
