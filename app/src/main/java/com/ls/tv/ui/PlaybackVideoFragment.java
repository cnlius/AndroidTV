package com.ls.tv.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;

import com.ls.tv.model.Movie;
import com.ls.tv.ui.presenter.CardPresenter;
import com.ls.tv.ui.presenter.DetailsDescriptionPresenter;
import com.ls.tv.utils.DataUtils;
import com.ls.tv.utils.LogUtils;

/**
 * 播放主控页面
 * Handles video playback with media controls.
 * Created by liusong on 2018/1/17.
 */

public class PlaybackVideoFragment extends VideoSupportFragment {
    private Movie mSelectedMovie;
    private ArrayObjectAdapter mRowsAdapter; // 总容器

    private PlaybackControlsRow mPlaybackControlsRow;
    private ArrayObjectAdapter mPrimaryActionsAdapter; // 主要按钮UI
    private ArrayObjectAdapter mSecondaryActionsAdapter; //次要按钮UI

    //控制动作
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction; //暂停
    private PlaybackControlsRow.RepeatAction mRepeatAction; // 重复
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction; //满意，赞
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction; //反对，取消赞
    private PlaybackControlsRow.ShuffleAction mShuffleAction; //搁置
    private PlaybackControlsRow.SkipNextAction mSkipNextAction; //跳过下一个
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction; //跳过前一个
    private PlaybackControlsRow.FastForwardAction mFastForwardAction; //快进
    private PlaybackControlsRow.RewindAction mRewindAction; //回退
    private PlaybackControlsRow.HighQualityAction mHighQualityAction; //高质量
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction; //隐藏字幕
    private PlaybackControlsRow.MoreActions mMoreActions; //更多


    private void receivedData() {
        mSelectedMovie = getActivity().getIntent().getParcelableExtra(PlaybackActivity.MOVIE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.i(this, "onCreate");
        super.onCreate(savedInstanceState);
        receivedData();
        //背景类型：高亮透明背景
        setBackgroundType(VideoSupportFragment.BG_LIGHT);

        //播放时自动隐藏控制界面
        setFadingEnabled(true);
//        setControlsOverlayAutoHideEnabled(true);

        setUpRows();
    }

    /**
     * 设置内容行
     */
    private void setUpRows() {
        ClassPresenterSelector ps = new ClassPresenterSelector();
        //控制界面UI
        PlaybackControlsRowPresenter playbackControlsRowPresenter = new PlaybackControlsRowPresenter(new DetailsDescriptionPresenter());

        //添加控制row
        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        //添加ListRow
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());

        //构建总容器
        mRowsAdapter = new ArrayObjectAdapter(ps);

        //第一行：添加控制行；
        addPlaybackControlsRow();
        //第二行：推荐视频列表
        addOtherRows();

        setAdapter(mRowsAdapter);
    }

    /**
     * 控制行
     */
    private void addPlaybackControlsRow() {
        //总控制容器UI
        mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        //添加到总容器
        mRowsAdapter.add(mPlaybackControlsRow);

        //控制控件容器构建
        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        //主要按钮UI
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        //次要按钮UI
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);

        //将两个控制行ui添加到总控
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        //初始化各种控制动作
        Activity activity = getActivity();
        mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(activity);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(activity);
        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(activity);
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(activity);
        mShuffleAction = new PlaybackControlsRow.ShuffleAction(activity);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(activity);
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(activity);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(activity);
        mRewindAction = new PlaybackControlsRow.RewindAction(activity);
        mHighQualityAction = new PlaybackControlsRow.HighQualityAction(activity);
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(activity);
        mMoreActions = new PlaybackControlsRow.MoreActions(activity);

        /* PrimaryAction setting(控制动作装填到主要控制UI) */
        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mFastForwardAction);
        mPrimaryActionsAdapter.add(mSkipNextAction);

        /* SecondaryAction setting(控制动作装填到次要控制UI) */
        mSecondaryActionsAdapter.add(mThumbsUpAction);
        mSecondaryActionsAdapter.add(mThumbsDownAction);
        mSecondaryActionsAdapter.add(mRepeatAction);
        mSecondaryActionsAdapter.add(mShuffleAction);
        mSecondaryActionsAdapter.add(mHighQualityAction);
        mSecondaryActionsAdapter.add(mClosedCaptioningAction);
        mSecondaryActionsAdapter.add(mMoreActions);
    }

    /**
     * 其他内容列表
     */
    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int i = 0; i < 6; i++) {
            Movie movie = new Movie();
            DataUtils.setCardImageUrl(i, movie);
            movie.setTitle("title" + i);
            movie.setStudio("studio" + i);
            movie.setDescription("description" + i);
            listRowAdapter.add(movie);
        }

        HeaderItem header = new HeaderItem(0, "OtherRows");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }


}
