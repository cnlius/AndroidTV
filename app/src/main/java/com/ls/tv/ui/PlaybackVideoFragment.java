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
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;

    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.RepeatAction mRepeatAction;
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private PlaybackControlsRow.ShuffleAction mShuffleAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.HighQualityAction mHighQualityAction;
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    private PlaybackControlsRow.MoreActions mMoreActions;


    private void receivedData() {
        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);
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
        PlaybackControlsRowPresenter playbackControlsRowPresenter= new PlaybackControlsRowPresenter(new DetailsDescriptionPresenter());

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
        mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        mRowsAdapter.add(mPlaybackControlsRow);

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

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

        /* PrimaryAction setting */
        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mFastForwardAction);
        mPrimaryActionsAdapter.add(mSkipNextAction);

        /* SecondaryAction setting */
        mSecondaryActionsAdapter.add(mThumbsUpAction);
        mSecondaryActionsAdapter.add(mThumbsDownAction);
        mSecondaryActionsAdapter.add(mRepeatAction);
        mSecondaryActionsAdapter.add(mShuffleAction);
        mSecondaryActionsAdapter.add(mHighQualityAction);
        mSecondaryActionsAdapter.add(mClosedCaptioningAction);
        mSecondaryActionsAdapter.add(mMoreActions);
    }

    /**
     * 列表视频
     */
    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        Movie movie = new Movie();
        movie.setTitle("Title");
        movie.setStudio("studio");
        movie.setDescription("description");
        movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
        listRowAdapter.add(movie);
        listRowAdapter.add(movie);

        HeaderItem header = new HeaderItem(0, "OtherRows");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }


}
