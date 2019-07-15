package com.easy.tvbox.ui.music;

import android.util.TypedValue;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.FocusBorderHelper;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.MusicBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.owen.focus.FocusBorder;

import java.util.List;

import javax.inject.Inject;

public class MusicActivity extends BaseActivity<MusicBinding> implements MusicView, FocusBorderHelper {

    @Inject
    MusicPresenter musicPresenter;
    Account account;
    MusicFragment musicFragment, mvFragment;
    FocusBorder mFocusBorder;

    @Override
    public int getLayoutId() {
        return R.layout.music;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(musicPresenter);
    }

    @Override
    public void initView() {
        account = DataManager.getInstance().queryAccount();
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

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(MusicActivity.this)) {
                networkChange(true);
            }
        });
        mViewBinding.tvMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(1);
            }
        });
        mViewBinding.tvMusic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        mViewBinding.tvMv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(2);
            }
        });
        mViewBinding.tvMv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        musicFragment = MusicFragment.getInstance(1);
        mvFragment = MusicFragment.getInstance(2);

        setFragment(1);
        mFocusBorder.setVisible(true);
        onMoveFocusBorder(mViewBinding.tvMusic, 1.1f);
        networkChange(NetworkUtils.isNetConnected(MusicActivity.this));
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }


    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.flContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.flContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    public void setFragment(int currentType) {
        Fragment current = getSupportFragmentManager().findFragmentByTag(currentType + "");
        FragmentTransaction mFt = getSupportFragmentManager().beginTransaction();
        if (current == null) {
            if (currentType == 1) {
                mFt.add(R.id.flContain, musicFragment, String.valueOf(currentType));
            } else {
                mFt.add(R.id.flContain, mvFragment, String.valueOf(currentType));
            }
        } else {
            mFt.attach(current);
        }
        if (currentType == 1) {
            musicFragment.choose(true);
            musicFragment.choose(false);
        } else {
            musicFragment.choose(false);
            musicFragment.choose(true);
        }
        mFt.commit();
    }

    @Override
    public FocusBorder getFocusBorder() {
        return mFocusBorder;
    }
}
