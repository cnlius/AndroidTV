package com.ls.tv.ui.presenter;

import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;

import com.ls.tv.utils.LogUtils;

/**
 * 详情的设计:FullWidthDetailsOverviewRowPresenter
 * 1.Logo view：可定制（可选），通过实现DetailsOverViewLogoPresenter
 * 2.Action list view:通过点击可以查看不同的信息，如视频简介，演员表等。
 * 3.详细的说明view：可定制（必须），是 AbstractDetailsDescriptionPresenter 的子类
 * Created by liusong on 2018/1/15.
 */

public class CustomFullWidthDetailsOverviewRowPresenter extends FullWidthDetailsOverviewRowPresenter {

    /**
     * descriptionPresenter
     * @param presenter
     */
    public CustomFullWidthDetailsOverviewRowPresenter(Presenter presenter) {
        super(presenter);
    }

    @Override
    protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder vh) {
        LogUtils.i(this, "onRowViewAttachedToWindow");
        super.onRowViewAttachedToWindow(vh);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        LogUtils.i(this, "onBindRowViewHolder");
        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void onLayoutOverviewFrame(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        LogUtils.i(this, "onLayoutOverviewFrame");

        /* Please try selecting either one. */
        //setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
        //setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_FULL);
        setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_HALF);  // Default behavior

        super.onLayoutOverviewFrame(viewHolder, oldState, logoChanged);
    }
}
