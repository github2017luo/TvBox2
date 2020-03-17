package com.easy.tvbox.ui.test;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.TestData;
import com.easy.tvbox.databinding.TestBinding;
import com.easy.tvbox.tvview.tvRecycleView.SimpleOnItemListener;
import com.easy.tvbox.tvview.tvRecycleView.TvRecyclerView;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewAdapter;
import com.easy.tvbox.tvview.tvwidget.CommonRecyclerViewHolder;
import com.easy.tvbox.utils.DisplayAdaptive;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity<TestBinding> implements TestView{

    @Inject
    TestPresenter testPresenter;
    UpdateDataAdapter mV7GridAdapter;
    FocusBorder mFocusBorder;

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
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.touming)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .shadowColorRes(R.color.touming)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .noShimmer()
                .build(this);

        mViewBinding.recyclerView.setSpacingWithMargins(20, 20);
        mV7GridAdapter = new UpdateDataAdapter(this);
        mV7GridAdapter.setDatas(getDatas());
        mViewBinding.recyclerView.setAdapter(mV7GridAdapter);


        mViewBinding.recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus);
            }
        });

        mViewBinding.recyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
//                float radius = DisplayAdaptive.getInstance().toLocalPx(10);
//                onMoveFocusBorder(itemView, 1.1f, radius);
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

    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }

    public List<TestData> getDatas() {
        List<TestData> testDatas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            TestData testData = new TestData();
            testData.setName("name:" + i);
            testDatas.add(testData);
        }
        return testDatas;
    }

    private class UpdateDataAdapter extends CommonRecyclerViewAdapter<TestData> {

        public UpdateDataAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.test_item;
        }

        @Override
        public void onBindItemHolder(CommonRecyclerViewHolder helper, TestData item, int position) {
            helper.getHolder().setText(R.id.tvTest, item.getName());
        }
    }
}
