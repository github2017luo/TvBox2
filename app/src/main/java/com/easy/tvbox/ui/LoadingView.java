package com.easy.tvbox.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easy.tvbox.R;

/**
 * Created by davy on 2018/3/9.
 */

public class LoadingView extends LinearLayout {
    public static final int STATUS_HIDDEN = 0;//不显示
    public static final int STATUS_LOADING = 1;//开始加载
    public static final int STATUS_NODATA = 2;//无数据
    public static final int STATUS_NONETWORK = 3;//无网络

    private TextView tvTip, tvRetry;
    LinearLayout loadErrorView;
    ProgressBar loadingProgressBar;

    public LoadingView(Context context) {
        this(context, null);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_view, null);
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        tvTip = findViewById(R.id.tvTip);
        tvRetry = findViewById(R.id.tvRetry);
        loadErrorView = findViewById(R.id.loadErrorView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
    }

    public void setStatus(int status) {
        switch (status) {
            case STATUS_HIDDEN:
                setVisibility(GONE);
                break;
            case STATUS_LOADING:
                loadErrorView.setVisibility(GONE);
                loadingProgressBar.setVisibility(VISIBLE);
                setVisibility(VISIBLE);
                break;
            case STATUS_NODATA:
                tvTip.setText("暂无数据");
                loadErrorView.setVisibility(VISIBLE);
                loadingProgressBar.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case STATUS_NONETWORK:
                tvTip.setText("网络异常，请检查网络连接后重试");
                loadErrorView.setVisibility(VISIBLE);
                loadingProgressBar.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
        }
    }

    public void setRetryListener(OnClickListener onClickListener) {
        tvRetry.setOnClickListener(onClickListener);
//        loadingProgressBar.setOnClickListener(onClickListener);
    }
}
