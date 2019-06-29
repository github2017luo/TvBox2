package com.easy.tvbox.ui.music;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseFragment;
import com.easy.tvbox.bean.MusicData;

import java.util.List;

import javax.inject.Inject;

public class MusicFragment extends BaseFragment implements  MusicFragmentView{

    @Inject
    MusicPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.music_fragment;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List observerList) {
        observerList.add(presenter);
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void queryMusicCallback(MusicData musicData) {

    }
}


