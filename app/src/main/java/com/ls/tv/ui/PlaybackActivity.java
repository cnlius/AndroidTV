package com.ls.tv.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.ls.tv.R;

/**
 * 播放页
 * 布局：
 * 1> VideoView是我们将播放视频内容的视图;
 * 2> PlaybackVideoFragment将显示用于控制视频的UI;
 * Created by liusong on 2018/1/17.
 */

public class PlaybackActivity extends FragmentActivity {
    public static final String MOVIE = "Movie";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_overlay);

        PlaybackVideoFragment playbackVideoFragment = new PlaybackVideoFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container_controls, playbackVideoFragment).commitAllowingStateLoss();
    }
}
