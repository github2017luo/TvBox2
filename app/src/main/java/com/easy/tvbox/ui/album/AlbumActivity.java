package com.easy.tvbox.ui.album;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.Album;
import com.easy.tvbox.bean.AlbumList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.AlbumBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class AlbumActivity extends BaseActivity<AlbumBinding> implements AlbumView {

    @Inject
    AlbumPresenter presenter;
    AlbumGridFragment albumGridFragment;

    @Override
    public int getLayoutId() {
        return R.layout.album;
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
        albumGridFragment = new AlbumGridFragment();
        albumGridFragment.setPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cardsFragment, albumGridFragment)
                .commit();

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(AlbumActivity.this)) {
                networkChange(true);
            }
        });
        networkChange(NetworkUtils.isNetConnected(AlbumActivity.this));
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.cardsFragment.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.cardsFragment.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void querySongSheetCallback(Respond<Album> respond) {
        if (respond.isOk()) {
            Album album = respond.getObj();
            if (album != null) {
                List<AlbumList> temp = album.getContent();
                if (temp != null && temp.size() > 0 && albumGridFragment != null) {
                    albumGridFragment.setData(temp);
                }
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }
}
