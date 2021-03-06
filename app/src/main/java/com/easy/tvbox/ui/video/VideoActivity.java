package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.easy.aliplayer.view.AliyunVodPlayerView;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.VideoBinding;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class VideoActivity extends BaseActivity<VideoBinding> implements VideoView {
    AliyunVodPlayerView mAliyunVodPlayerView = null;
    @Inject
    VideoPresenter presenter;
    LiveList liveData;
    TextView tvTitle;

    @Override
    public int getLayoutId() {
        return R.layout.video;
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
        observerList.add(presenter);
    }

    @Override
    public void initView() {
        Account account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }
        Intent intent = getIntent();
        String dataJson = intent.getStringExtra("data");
        mAliyunVodPlayerView = findViewById(com.easy.aliplayer.R.id.videoView);
        tvTitle = findViewById(R.id.tvTitle);
        try {
            liveData = JSON.parseObject(dataJson, LiveList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (liveData == null) {
            finish();
            return;
        }
        tvTitle.setText(liveData.getTitle());
        initAliyunPlayerView();
//        startPayer("http://mobile.hxsoft.net/live/show.m3u8");
        presenter.getLivePlayUrl(liveData.getUid());
    }

    private void initAliyunPlayerView() {
        mAliyunVodPlayerView = findViewById(com.easy.aliplayer.R.id.videoView);
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnCompletionListener(() -> playerCompletion());
        mAliyunVodPlayerView.setOnErrorListener(errorInfo -> playerError(errorInfo));
        mAliyunVodPlayerView.enableNativeLog();
    }

    private void playerError(ErrorInfo errorInfo) {
        Log.d("VcPlayer", errorInfo.getMsg());
    }

    /**
     * 播放完成
     */
    private void playerCompletion() {
        finish();
    }

    private void startPayer(String url) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        mAliyunVodPlayerView.setLocalSource(urlSource);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (tvTitle != null && tvTitle.getVisibility() == View.GONE) {
                tvTitle.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (tvTitle != null && tvTitle.getVisibility() == View.VISIBLE) {
            tvTitle.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(false);
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(true);
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    public void livePlayUrlCallback(Respond<String> respond) {
        if (respond.isOk()) {
            String uri = respond.getObj();
            if (!TextUtils.isEmpty(uri)) {
                startPayer(uri);
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        super.onDestroy();
    }
}
