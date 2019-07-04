package com.easy.tvbox.ui.test;

import androidx.fragment.app.Fragment;

import com.easy.tvbox.MainFragment;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.databinding.TestBinding;

import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity<TestBinding> implements TestView {

    @Inject
    TestPresenter testPresenter;

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
        Fragment fragment = new TestFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment)
                .commit();
    }

}
