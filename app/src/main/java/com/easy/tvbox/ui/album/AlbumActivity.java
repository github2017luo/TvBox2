package com.easy.tvbox.ui.album;

import android.util.TypedValue;
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
import com.easy.tvbox.tvview.tvRecycleView.SimpleOnItemListener;
import com.easy.tvbox.tvview.tvRecycleView.TvRecyclerView;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;

import java.util.List;

import javax.inject.Inject;

public class AlbumActivity extends BaseActivity<AlbumBinding> implements AlbumView{

    @Inject
    AlbumPresenter presenter;
    AlbumAdapter albumAdapter;
    FocusBorder mFocusBorder;
    Account account;

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
            if (NetworkUtils.isNetConnected(AlbumActivity.this)) {
                networkChange(true);
            }
        });

        mViewBinding.recyclerView.setSpacingWithMargins(10, 3);
        albumAdapter = new AlbumAdapter(this);
        mViewBinding.recyclerView.setAdapter(albumAdapter);
        mViewBinding.recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus);
            }
        });

        mViewBinding.recyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, 1.1f);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                ToastUtils.showLong("onItemClick::"+position);
            }
        });
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.recyclerView.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
            presenter.querySongSheet(account.getShopNo());
        } else {
            mViewBinding.recyclerView.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void querySongSheetCallback(Respond<Album> respond) {
        if (respond.isOk()) {
            Album album = respond.getObj();
            if (album != null) {
                List<AlbumList> temp = album.getContent();
                if (temp != null && temp.size() > 0 && albumAdapter!=null ) {
                    albumAdapter.clearDatas();
                    albumAdapter.appendDatas(temp);
                    mViewBinding.recyclerView.getNextFocusDownId();
                }
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }
}
