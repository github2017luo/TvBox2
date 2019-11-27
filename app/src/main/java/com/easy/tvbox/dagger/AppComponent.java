package com.easy.tvbox.dagger;


import com.easy.tvbox.ui.album.AlbumActivity;
import com.easy.tvbox.ui.album.AlbumListActivity;
import com.easy.tvbox.ui.daily.DailyActivity;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.ui.live.LiveActivity;
import com.easy.tvbox.ui.login.LoginActivity;
import com.easy.tvbox.ui.mine.MineActivity;
import com.easy.tvbox.ui.music.MusicActivity;
import com.easy.tvbox.ui.music.MusicDetailActivity;
import com.easy.tvbox.ui.music.MusicFragment;
import com.easy.tvbox.ui.phone.UpdatePhoneActivity;
import com.easy.tvbox.ui.test.TestActivity;
import com.easy.tvbox.ui.video.DailyVideoActivity;
import com.easy.tvbox.ui.video.MusicVideoActivity;
import com.easy.tvbox.ui.video.VideoActivity;

import dagger.Component;

/**
 * Created by clarence on 2018/3/26
 */
@ActivityScope
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(LoginActivity activity);

    void inject(VideoActivity activity);

    void inject(UpdatePhoneActivity activity);

    void inject(HomeActivity activity);

    void inject(MusicActivity activity);

    void inject(MusicDetailActivity activity);

    void inject(MineActivity activity);

    void inject(LiveActivity activity);

    void inject(DailyActivity activity);

    void inject(DailyVideoActivity activity);

    void inject(MusicFragment fragment);

    void inject(MusicVideoActivity fragment);

    void inject(TestActivity fragment);

    void inject(AlbumActivity activity);

    void inject(AlbumListActivity activity);

}
