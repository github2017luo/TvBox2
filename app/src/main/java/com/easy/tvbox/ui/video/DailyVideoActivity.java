package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.easy.tvbox.bean.PlayProgress;
import com.easy.tvbox.databinding.DailyVideoBinding;
import com.easy.tvbox.event.MtMessage;
import com.easy.tvbox.utils.DimensUtils;
import com.easy.tvbox.utils.ToastUtils;
import com.easy.tvbox.view.PlayerControlView;
import com.easy.tvbox.view.PlayerView;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.owen.focus.FocusBorder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Retrofit;

import static com.easy.tvbox.view.PlayerView.SHOW_BUFFERING_WHEN_PLAYING;

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
    int currentPlayPosition = 0;
    DefaultControlDispatcher dispatcher;
    boolean isFirstLoad = true;
    Disposable disposable, playProgress;
    private long fistTouchTime;

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
                .borderColorRes(R.color.touming)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 0.01f)
                .shadowColorRes(R.color.touming)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 0.01f)
                .build(this);
        width = DimensUtils.dp2px(this, 150);
        Intent intent = getIntent();
        String dataJson = intent.getStringExtra("data");
        int startPosition = -1;
        try {
            Daily daily = JSON.parseObject(dataJson, Daily.class);
            if (daily != null) {
                dailyItems = daily.getDailyItems();
                List<PlayProgress> playProgresses = DataManager.getInstance().queryPlayProgress();
                if (playProgresses != null && playProgresses.size() > 0) {
                    for (int i = 0; i < dailyItems.size(); i++) {
                        DailyItem dailyItem = dailyItems.get(i);
                        for (PlayProgress playProgress : playProgresses) {
                            if (dailyItem.getVid().equals(playProgress.getId())) {
                                dailyItem.setProgress(playProgress.getProgressInt());
                                dailyItem.setFinish(playProgress.isFinish());
                            }
                        }
                        Log.d("SeekTo", "i=" + i + " vid:" + dailyItem.getVid() + " finish:" + dailyItem.isFinish());
                        if (startPosition == -1 && !dailyItem.isFinish()) {
                            startPosition = i;
                            Log.d("SeekTo", "选中startPosition=" + startPosition);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (startPosition == -1) {
            startPosition = 0;
        }
        if (dailyItems == null || dailyItems.isEmpty()) {
            finish();
            return;
        }
        addVideoView();
        initExoPlayer();
        DailyItem dailyItem = dailyItems.get(startPosition);
        findPlayPosition(dailyItems, startPosition, dailyItem.getProgress());
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
            dailyItem.setPosition(i);
            View item = LayoutInflater.from(this).inflate(R.layout.video_item, null);
            item.setTag(dailyItem);
            ProgressBar progressBar = item.findViewById(R.id.progressBar);
            progressBar.setMax(dailyItem.getDurationMForLong());
            item.setNextFocusUpId(R.id.exo_ffwd);
            if (i == 0) {
                item.setId(R.id.video_vew_id);
                item.setNextFocusLeftId(R.id.exo_ffwd);
                progressBar.setProgressDrawable(getDrawable(R.drawable.progress_horizontal_red));
            }
            progressBar.setProgress(dailyItem.getProgress());
            item.setOnClickListener(v -> clickVideo(v, false));
            item.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
            ImageView videoPreImage = item.findViewById(R.id.videoPreImage);
            Glide.with(this).load(dailyItem.getFaceurl()).into(videoPreImage);
            TextView videoTitle = item.findViewById(R.id.videoTitle);
            videoTitle.setText(TextUtils.isEmpty(dailyItem.getTitle()) ? "无标题" : dailyItem.getTitle());
            scrollView.addView(item, width, width);
        }
    }

    private void clickVideo(View item, boolean force) {
        Object object = item.getTag();
        if (object instanceof DailyItem) {
            DailyItem dailyItem = (DailyItem) object;
            if (!force && dailyItem.getPosition() == currentPlayPosition) {
                return;
            }
            if (dispatcher != null) {
                dispatcher.dispatchSeekTo(player, dailyItem.getPosition(), dailyItem.getProgress());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (playerView != null && !playerView.isControllerVisible()) {
                playerView.showController();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startInterval() {
        Observable.interval(0, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable dis) {
                        disposable = dis;
                    }

                    @Override
                    public void onNext(Long number) {
                        if (player != null && dailyItems != null && scrollView != null) {
                            if (player.getCurrentPosition() > 100 && scrollView.getChildCount() < player.getCurrentWindowIndex()) {
                                View view = scrollView.getChildAt(player.getCurrentWindowIndex());
                                if (view != null) {
                                    ProgressBar progressBar = view.findViewById(R.id.progressBar);
                                    progressBar.setProgress((int) player.getCurrentPosition());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });
    }

    public void seekTo(View item, DailyItem dailyItem, int windowIndex, long positionMs) {
        if (currentPlayPosition == windowIndex) {
            Log.d("SeekTo", "没切换视频");
            dailyItem.setProgress((int) positionMs);
            if (mFocusBorder.isVisible() && item != null) {
                ProgressBar progressBar = item.findViewById(R.id.progressBar);
                progressBar.setProgress(dailyItem.getProgress());
            }
        } else {
            Log.d("SeekTo", "切换视频");
            DailyItem lastItem = dailyItems.get(currentPlayPosition);
            if (scrollView.getChildCount() < currentPlayPosition) {
                View lastView = scrollView.getChildAt(currentPlayPosition);
                lastItem.setProgress((int) player.getCurrentPosition());
                if (mFocusBorder.isVisible() && lastView != null) {
                    ProgressBar progressBar = lastView.findViewById(R.id.progressBar);
                    progressBar.setProgressDrawable(getDrawable(R.drawable.progress_horizontal2));
                    progressBar.setProgress(lastItem.getProgress());
                }
            }

            currentPlayPosition = windowIndex;
            if (scrollView.getChildCount() < currentPlayPosition) {
                View currentView = scrollView.getChildAt(currentPlayPosition);
                ProgressBar currentProgressBar = currentView.findViewById(R.id.progressBar);
                currentProgressBar.setProgressDrawable(getDrawable(R.drawable.progress_horizontal_red));
            }
            if (mFocusBorder.isVisible() && item != null) {
                onMoveFocusBorder(item, 1.1f);
            }
            positionMs = dailyItem.getProgress();
        }

        player.seekTo(windowIndex, positionMs);
        Log.d("SeekTo", "windowIndex:" + windowIndex + " positionMs:" + positionMs);
        Log.d("SeekTo", dailyItem.toString());
    }

    private void switchNextVideo(boolean isFinish) {
        Log.d("SeekTo", "切换视频===》lastPlayPosition：" + currentPlayPosition + " position:" + player.getCurrentPosition());
        DailyItem lastItem = dailyItems.get(currentPlayPosition);
        View lastView = scrollView.getChildAt(currentPlayPosition);
        ProgressBar progressBar = lastView.findViewById(R.id.progressBar);
        progressBar.setProgressDrawable(getDrawable(R.drawable.progress_horizontal2));
        if (isFinish) {
            lastItem.setProgress(lastItem.getDurationMForLong());
            lastItem.setFinish(true);
            progressBar.setProgress(lastItem.getProgress());
        }

        currentPlayPosition = player.getCurrentWindowIndex();
        View currentView = scrollView.getChildAt(currentPlayPosition);
        ProgressBar currentProgressBar = currentView.findViewById(R.id.progressBar);
        currentProgressBar.setProgressDrawable(getDrawable(R.drawable.progress_horizontal_red));
        if (isFinish) {
            if (controlView.getVisibility() == View.VISIBLE) {
                onMoveFocusBorder(currentView, 1.1f);
            }
        }
    }

    private void initExoPlayer() {
        playerView = mViewBinding.videoView;
        playerView.setShowMultiWindowTimeBar(false);
        playerView.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING);
        controlView = findViewById(R.id.exo_controller);
        View playBtn = controlView.findViewById(R.id.exo_play);
        playBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.5f));
        View pauseBtn = controlView.findViewById(R.id.exo_pause);
        pauseBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.5f));

        View rewBtn = controlView.findViewById(R.id.exo_rew);
        rewBtn.setOnKeyListener((v, keyCode, event) -> {
            Log.d("LongClick", "keyCode:" + keyCode + " action:" + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_UP
                    && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                cancelPlayProgress();
                Log.d("LongClick", " rewBtn==>up");
            }
            return false;
        });
        rewBtn.setOnLongClickListener(v -> {
            Log.d("LongClick", " rewBtn  => LongClick");
            changePlayProgress(false);
            return false;
        });
        rewBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.5f));
        View ffwdBtn = controlView.findViewById(R.id.exo_ffwd);
        ffwdBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.5f));
        ffwdBtn.setOnKeyListener((v, keyCode, event) -> {
            Log.d("LongClick", "keyCode:" + keyCode + " action:" + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_UP
                    && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                cancelPlayProgress();
                Log.d("LongClick", " ffwdBtn==>up");
            }
            return false;
        });
        ffwdBtn.setOnLongClickListener(v -> {
            Log.d("LongClick", " ffwdBtn => LongClick");
            changePlayProgress(true);
            return false;
        });
        playerView.setControllerShowTimeoutMs(8000);
        dispatcher = new DefaultControlDispatcher() {
            @Override
            public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
                if (scrollView != null) {
                    View view = scrollView.getChildAt(windowIndex);
                    if (view != null) {
                        Object object = view.getTag();
                        if (object instanceof DailyItem) {
                            DailyItem dailyItem = (DailyItem) object;
                            seekTo(view, dailyItem, windowIndex, positionMs);
                        }
                    }
                } else {
                    player.seekTo(windowIndex, positionMs);
                }
                return true;
            }
        };
        playerView.setControlDispatcher(dispatcher);
        playerView.setControllerVisibilityListener(visibility -> {
            if (mFocusBorder != null) {
                mFocusBorder.setVisible(View.VISIBLE == visibility);
            }
            if (View.VISIBLE == visibility) {
                if (playBtn.hasFocus()) {
                    playBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Focustv", "playBtn.onMoveFocusBorder");
                            onMoveFocusBorder(playBtn, 1.5f);
                        }
                    }, 200);
                } else {
                    pauseBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Focustv", "pauseBtn.onMoveFocusBorder");
                            onMoveFocusBorder(pauseBtn, 1.5f);
                        }
                    }, 200);
                }
            }
        });
        eventListener = new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Log.d("SeekTo", "onTimelineChanged:");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d("SeekTo", "onTracksChanged:");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d("SeekTo", "onLoadingChanged:");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                String stateString;
                // actually playing media
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d("SeekTo", "onPlayerStateChanged: actually playing media");
                }
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        stateString = "ExoPlayer.STATE_IDLE      -";
                        break;
                    case Player.STATE_BUFFERING:
                        stateString = "ExoPlayer.STATE_BUFFERING -";
                        if (isFirstLoad) {
                            isFirstLoad = false;
                            startInterval();
                        }
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
                Log.d("SeekTo", "changed state to " + stateString + " playWhenReady: " + playWhenReady);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d("SeekTo", "onRepeatModeChanged:");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.d("SeekTo", "onShuffleModeEnabledChanged:");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("SeekTo", "onPlayerError:");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d("SeekTo", "onPositionDiscontinuity:reason==>" + reason);
                switchNextVideo(reason == 0);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d("SeekTo", "onPlaybackParametersChanged:");
            }

            @Override
            public void onSeekProcessed() {
                Log.d("SeekTo", "onSeekProcessed:");
            }
        };
        player = ExoPlayerFactory.newSimpleInstance(this);
        player.addListener(eventListener);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        playerView.hideController();
    }

    public void changePlayProgress(boolean isForward) {
        Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        playProgress = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d("LongClick", "onNext:" + aLong);
                        if (player != null && player.isCurrentWindowSeekable()) {
                            player.seekTo(player.getCurrentPosition() + (isForward ? 1 : -1) * 5000);
                            Log.d("LongClick", "CurrentPosition:" + player.getCurrentPosition());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("LongClick", "onError");
                        cancelPlayProgress();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("LongClick", "onComplete");
                        cancelPlayProgress();
                    }
                });
    }

    public void cancelPlayProgress() {
        if (playProgress != null && !playProgress.isDisposed()) {
            playProgress.dispose();
        }
    }

    public void startPayer(List<String> urls, int chooseVideoPosition, long positionMs) {
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
                player.seekTo(chooseVideoPosition, positionMs);
                Log.d("SeekTo", "开始定位chooseVideoPosition:" + chooseVideoPosition + " positionMs:" + positionMs);
            }
        }
    }

    /**
     * 查找播放的位置
     */
    private void findPlayPosition(List<DailyItem> items, int chooseVideoPosition, long position) {
        List<String> urls = new ArrayList<>();
        for (DailyItem dailyItem : items) {
            urls.add(dailyItem.getVideourl());
        }
        startPayer(urls, chooseVideoPosition, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayEvent(MtMessage message) {
        if (message != null && dailyItems != null && dailyItems.size() > 0) {
            int choosePosition = -1;
            for (int i = 0; i < dailyItems.size(); i++) {
                if (dailyItems.get(i).getVid().equals(message.getVid())) {
                    choosePosition = i;
                    break;
                }
            }
            if (choosePosition != -1) {
                findPlayPosition(dailyItems, choosePosition, message.getTimer());
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        cancelPlayProgress();
        if (dailyItems != null && player != null) {
            dailyItems.get(player.getCurrentWindowIndex()).setProgress((int) player.getCurrentPosition());
            List<PlayProgress> playProgresses = new ArrayList<>();
            for (DailyItem dailyItem : dailyItems) {
                PlayProgress playProgress = new PlayProgress();
                playProgress.setId(dailyItem.getVid());
                playProgress.setFinish(dailyItem.isFinish());
                playProgress.setProgress(dailyItem.getProgress() + "");
                playProgresses.add(playProgress);
            }
            DataManager.getInstance().updateProgress(playProgresses);
        }
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        if (controlView != null && controlView.getVisibility() == View.VISIBLE) {
            controlView.hide();
            return;
        }
        long nowTouchTime = System.currentTimeMillis();
        if (nowTouchTime - fistTouchTime < 2000) {//
            super.onBackPressed();
            finish();
        } else {
            fistTouchTime = nowTouchTime;
            ToastUtils.showShort("再次点击退出播放");
        }
    }
}
