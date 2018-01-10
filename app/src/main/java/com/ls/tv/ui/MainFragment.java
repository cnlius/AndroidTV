package com.ls.tv.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ls.tv.R;
import com.ls.tv.utils.LogUtils;

/**
 * MainFragment由左侧抽屉样式的标题区和右侧内容区组成
 * BrowseFragment类由Android SDK Leanback库提供
 * Created by liusong on 2018/1/9.
 */

public class MainFragment extends BrowseFragment {
    private ArrayObjectAdapter mRowsAdapter; //主界面内容容器adapter
    /* Grid row item settings */
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.i(this, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        setupUIElements();
        loadRows();
    }

    /**
     * 设置MainFragment的主界面相关信息
     * 1> 由左侧抽屉样式的标题导航栏和右侧内容区组成
     * 2> 设置内容区的标题，显示在右上角；
     * 3> 导航栏显示样式：按back显示，选中时隐藏；
     * 4> 导航栏背景色设置；
     * 5> 搜索按钮图标颜色设置；
     */
    private void setupUIElements() {
        //setBadgeDrawable展示在标题栏上的图片会覆盖标题
        //setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        //设置title
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        //显示左侧导航栏，HEADERS_DISABLED 不显示 HEADERS_HIDDEN 隐藏，到边缘按左键还能显示
        setHeadersState(HEADERS_ENABLED);
        //back键会显示
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color(设置快速导航（或 headers) 背景色)
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color(设置搜索的颜色)
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    /**
     * 设置导航栏和内容区的内容
     * 1> 整个mainFragment看做是一个RowsAdapter(ArrayObjectAdapter)：由一组ListRow构成
     * 2> ListRow：headerItem对应左侧抽屉导航栏内的标题，每个标题对应的ArrayObjectAdapter是右侧内容界面
     */
    private void loadRows() {
        //主界面内容容器adapter
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        /* GridItemPresenter */
        HeaderItem gridItemPresenterHeader = new HeaderItem(0, "GridItemPresenter");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("ITEM 1");
        gridRowAdapter.add("ITEM 2");
        gridRowAdapter.add("ITEM 3");
        mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

        /* set */
        setAdapter(mRowsAdapter);
    }

    /**
     * 对应主界面左侧抽屉导航栏的标题组内的每个item的UI
     */
    private class GridItemPresenter extends Presenter {
        /* Grid row item settings */
        private static final int GRID_ITEM_WIDTH = 300;
        private static final int GRID_ITEM_HEIGHT = 200;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            Context context = parent.getContext();
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(context.getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }


}
