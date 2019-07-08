package com.easy.tvbox.ui.album;

import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.AlbumList;
import com.easy.tvbox.ui.live.LiveGridAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AlbumGridFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;
    AlbumPresenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRowAdapter();
    }

    public void setPresenter(AlbumPresenter presenter) {
        this.presenter = presenter;
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_XSMALL, true);
        gridPresenter.setNumberOfColumns(1);
        gridPresenter.setShadowEnabled(true);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new LiveGridAdapter(getActivity()));
        setAdapter(mAdapter);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof AlbumList) {

            }
        });

        if (presenter != null) {
            Account account = DataManager.getInstance().queryAccount();
            presenter.querySongSheet(account.getShopNo());
        }
    }

    public void setData(List<AlbumList> temp) {
        if (mAdapter != null) {
            prepareEntranceTransition();
            mAdapter.clear();
            mAdapter.addAll(0, temp);
            startEntranceTransition();
        }
    }
}
