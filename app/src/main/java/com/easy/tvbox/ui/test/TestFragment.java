package com.easy.tvbox.ui.test;

import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.easy.tvbox.R;
import com.easy.tvbox.ui.home.HomeMenuAdapter;
import com.google.gson.Gson;

public class TestFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRowAdapter();
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new HomeMenuAdapter(getActivity()));
        setAdapter(mAdapter);

        prepareEntranceTransition();

        String json = Utils.inputStreamToString(getResources().openRawResource(R.raw.grid_example));
        CardRow row = new Gson().fromJson(json, CardRow.class);
        mAdapter.addAll(0, row.getCards());

        startEntranceTransition();

    }
}
