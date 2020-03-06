package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.bean.DailyItem;
import com.easy.tvbox.databinding.DailyVideoBinding;
import com.easy.tvbox.event.MtMessage;
import com.easy.tvbox.utils.DimensUtils;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.owen.focus.FocusBorder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DailyVideoActivity extends BaseActivity<DailyVideoBinding> implements DailyVideoView {

    @Inject
    DailyVideoPresenter presenter;
    List<DailyItem> dailyItems;
    PlayerView playerView;
    ExoPlayer player;
    Player.EventListener eventListener;
    int width;
    FocusBorder mFocusBorder;
    PlayerControlView controlView;
    LinearLayout scrollView;

    @Override
    public int getLayoutId() {
        return R.layout.daily_video;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect && player != null) {
            player.retry();
        }
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
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);
        width = DimensUtils.dp2px(this, 150);
        Intent intent = getIntent();
        String dataJson = intent.getStringExtra("data");
        try {
            Daily daily = JSON.parseObject(dataJson, Daily.class);
            if (daily != null) {
                dailyItems = daily.getDailyItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dailyItems == null || dailyItems.isEmpty()) {
            finish();
            return;
        }
        initExoPlayer();
        findPlayPosition(dailyItems, 0);
        addVideoView();
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    private void addVideoView() {
        scrollView = findViewById(R.id.scrollView);
        for (int i = 0; i < dailyItems.size(); i++) {
            DailyItem dailyItem = dailyItems.get(i);
            View item = LayoutInflater.from(this).inflate(R.layout.video_item, null);
            item.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
            ImageView videoPreImage = item.findViewById(R.id.videoPreImage);
            Glide.with(this).load(dailyItem.getFaceurl()).into(videoPreImage);
            TextView videoTitle = item.findViewById(R.id.videoTitle);
            videoTitle.setText(TextUtils.isEmpty(dailyItem.getTitle()) ? "无标题" : dailyItem.getTitle());
            scrollView.addView(item, width, width);
            if (i == 0) {
                onMoveFocusBorder(item, 1.1f);
            }
        }
    }

    private void initExoPlayer() {
        playerView = mViewBinding.videoView;
        player = ExoPlayerFactory.newSimpleInstance(this);
        controlView = findViewById(R.id.exo_controller);
        View playBtn = controlView.findViewById(R.id.exo_play);
        playBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        View pauseBtn = controlView.findViewById(R.id.exo_pause);
        pauseBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        View rewBtn = controlView.findViewById(R.id.exo_rew);
        rewBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        View ffwdBtn = controlView.findViewById(R.id.exo_ffwd);
        ffwdBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        playerView.setControllerShowTimeoutMs(8000);
        DefaultControlDispatcher dispatcher = new DefaultControlDispatcher() {
            @Override
            public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
                player.seekTo(windowIndex, positionMs);
                if (mFocusBorder.isVisible() && scrollView.getChildCount() > 0) {
                    View view = scrollView.getChildAt(windowIndex);
                    onMoveFocusBorder(view, 1.1f);
                }
                Log.d("SeekTo", "windowIndex:" + windowIndex + " positionMs:" + positionMs);
                return true;
            }
        };
        playerView.setControlDispatcher(dispatcher);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        playerView.setControllerVisibilityListener(visibility -> {
            if (mFocusBorder != null) {
                mFocusBorder.setVisible(View.VISIBLE == visibility);
            }
        });
        eventListener = new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                String stateString;
                // actually playing media
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d("", "onPlayerStateChanged: actually playing media");
                }
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        stateString = "ExoPlayer.STATE_IDLE      -";
                        break;
                    case Player.STATE_BUFFERING:
                        stateString = "ExoPlayer.STATE_BUFFERING -";
                        break;
                    case Player.STATE_READY:
                        stateString = "ExoPlayer.STATE_READY     -";
                        break;
                    case Player.STATE_ENDED:
                        stateString = "ExoPlayer.STATE_ENDED     -";
                        finish();
                        break;
                    default:
                        stateString = "UNKNOWN_STATE             -";
                        break;
                }
                Log.d("DailyVideoActivity", "changed state to " + stateString + " playWhenReady: " + playWhenReady);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        };
        player.addListener(eventListener);
    }

    public void startPayer(List<String> urls, long positionMs) {
        if (urls != null && urls.size() > 0) {
            ConcatenatingMediaSource source = new ConcatenatingMediaSource();
            for (String url : urls) {
                Uri uri = Uri.parse(url);
                if (uri != null) {
                    MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
                    source.addMediaSource(mediaSource);
                }
            }
            if (source.getSize() > 0) {
                player.prepare(source);
                if (positionMs != 0) {
                    player.seekTo(positionMs);
                }
            }
        }
    }

    /**
     * 查找播放的位置
     */
    private void findPlayPosition(List<DailyItem> items, long position) {
        List<String> urls = new ArrayList<>();
        for (DailyItem dailyItem : items) {
            urls.add(dailyItem.getVideourl());
        }
        startPayer(urls, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayEvent(MtMessage message) {
        if (message != null && dailyItems != null && dailyItems.size() > 0) {
            List<DailyItem> newItems = new ArrayList<>();
            boolean add = false;
            for (DailyItem dailyItem : dailyItems) {
                if (dailyItem.getVid().equalsIgnoreCase(message.getVid())) {
                    add = true;
                }
                if (add) {
                    newItems.add(dailyItem);
                }
            }
            if (newItems.size() > 0) {
                findPlayPosition(newItems, message.getTimer());
            }
        }
    }

    private void releasePlayer() {
        if (player != null) {
            if (eventListener != null) {
                player.removeListener(eventListener);
            }
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
