package com.easy.tvbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.leanback.widget.VerticalGridView;

import com.easy.tvbox.R;

public class GridPresenter extends VerticalGridPresenter {

    public GridPresenter() {
        super(FocusHighlight.ZOOM_FACTOR_LARGE);
    }

    public GridPresenter(int focusZoomFactor, boolean useFocusDimmer) {
        super(focusZoomFactor, useFocusDimmer);
    }

    public GridPresenter(int focusZoomFactor) {
        super(focusZoomFactor, true);
    }

    @Override
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gride_view, parent, false);
        return new ViewHolder((VerticalGridView) root.findViewById(R.id.browse_grid));
    }
}
