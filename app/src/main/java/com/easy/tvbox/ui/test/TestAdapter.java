package com.easy.tvbox.ui.test;

import android.content.Context;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.easy.tvbox.R;
import com.easy.tvbox.base.GodBaseAdapter;
import com.easy.tvbox.base.ViewHolder;
import com.easy.tvbox.bean.TestData;

import java.util.List;

public class TestAdapter extends GodBaseAdapter<TestData> {

    public TestAdapter(Context context, List<TestData> datas) {
        super(context, datas);
    }

    @Override
    public void initItemView(View convertView, TestData itemData, int position) {
        ViewHolder.getView(convertView, R.id.rootView).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ViewCompat.animate(v).scaleX(1.5f).scaleY(1.5f).translationZ(1).start();
                } else {
                    ViewCompat.animate(v).scaleX(1f).scaleY(1f).translationZ(1).start();
                }
            }
        });
    }

    @Override
    protected int getItemLayout() {
        return R.layout.test_item;
    }
}
