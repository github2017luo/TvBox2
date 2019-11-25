package com.easy.tvbox.ui.music;

import android.content.Intent;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.bumptech.glide.Glide;
import com.easy.aliplayer.view.AliyunVodPlayerView;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.databinding.MusicDetailBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.MImageGetter;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;

import java.util.List;

import javax.inject.Inject;

import static com.easy.tvbox.base.Constant.OPEN_PLAYER;

public class MusicDetailActivity extends BaseActivity<MusicDetailBinding> implements MusicDetailView {

    @Inject
    MusicDetailPresenter presenter;
    int position;
    MusicList musicList;
    MusicInfo musicInfo;
    AliyunVodPlayerView mAliyunVodPlayerView = null;
    FocusBorder mFocusBorder;

    @Override
    public int getLayoutId() {
        return R.layout.music_detail;
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

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);

        if (MusicFragment.musicLists.size() > position && position >= 0) {
            musicList = MusicFragment.musicLists.get(position);
        }
        if (musicList == null) {
            finish();
            return;
        }

        initViewClick();

        if (OPEN_PLAYER) {
            initAliyunPlayerView();
        }

        networkChange(NetworkUtils.isNetConnected(MusicDetailActivity.this));

        startPayer();
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    private void initViewClick() {
        mViewBinding.tvLyric.setMovementMethod(ScrollingMovementMethod.getInstance());
        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(MusicDetailActivity.this)) {
                networkChange(true);
            }
        });
        mViewBinding.ivPrev.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.ivPrev.setOnClickListener(v -> {
            position--;
            if (position < 0) {
                ToastUtils.showLong("已经是第一首歌曲");
                position = 0;
                return;
            }
            startPayer();
        });

        mViewBinding.ivPlayer.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.ivPlayer.setOnClickListener(v -> {
            if (mAliyunVodPlayerView != null) {
                if (mAliyunVodPlayerView.isPlaying()) {
                    mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
                    mAliyunVodPlayerView.pause();
                } else {
                    if (musicInfo != null) {
                        mAliyunVodPlayerView.startPlay();
                        mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
                    }
                }
            }
        });

        mViewBinding.ivNext.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.ivNext.setOnClickListener(v -> {
            position++;
            if (position >= MusicFragment.musicLists.size()) {
                ToastUtils.showLong("已经是最后一首歌曲");
                position = MusicFragment.musicLists.size() - 1;
                return;
            }
            startPayer();
        });
    }

    private void startPayer() {
        if (position < 0 || position >= MusicFragment.musicLists.size()) {
            return;
        }
        musicList = MusicFragment.musicLists.get(position);
        if (musicList != null) {
            refreshView();
            if (mAliyunVodPlayerView != null) {
                musicInfo = musicList.getMusicInfo();
                if (musicInfo != null) {
                    mAliyunVodPlayerView.setDataSource(musicInfo.getPlayUrl());
                    mAliyunVodPlayerView.prepare();
                }
            }
        }
    }

    private void initAliyunPlayerView() {
        mAliyunVodPlayerView = mViewBinding.videoView;
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnCompletionListener(() -> playerCompletion());
        mAliyunVodPlayerView.setOnErrorListener(errorInfo -> playerError(errorInfo));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(() -> playerFirstFrameStart());
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(playerState -> playStateSwitch(playerState));
        mAliyunVodPlayerView.enableNativeLog();
    }

    /**
     * 播放状态切换
     */
    private void playStateSwitch(int playerState) {
        if (playerState == IPlayer.started) {//暂停
            mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            Log.d("VcPlayer", "playStateSwitch==>播放状态切换 playerState=" + playerState);
        } else if (playerState == IPlayer.paused) {//开始
            Log.d("VcPlayer", "playStateSwitch==>播放状态切换 playerState=" + playerState);
            mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
        }
    }

    private void playerFirstFrameStart() {
        Log.d("VcPlayer", "playerFirstFrameStart");
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
    }

    private void playerError(ErrorInfo errorInfo) {
        Log.d("VcPlayer", "playerError==>" + errorInfo.getMsg());
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
    }

    /**
     * 播放完成
     */
    private void playerCompletion() {
        Log.d("VcPlayer", "playerCompletion");
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    private void refreshView() {
        String pic = "";
        List<String> pics = musicList.getPictures();
        if (pics != null && pics.size() > 0) {
            pic = pics.get(0);
        }
        Glide.with(MusicDetailActivity.this)
                .load(pic).error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .centerCrop()
                .into(mViewBinding.ivIcon);
        mViewBinding.tvTitle.setText(musicList.getTitle());
        mViewBinding.tvGeshou.setText("演唱者：" + musicList.getGeshou());

        MImageGetter urlImageGetter = new MImageGetter(mViewBinding.tvLyric, MusicDetailActivity.this);//实例化URLImageGetter类
        mViewBinding.tvLyric.setText(Html.fromHtml(musicList.getLyric(), urlImageGetter, null));
        mViewBinding.tvLyric.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
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
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        super.onDestroy();
    }
}
