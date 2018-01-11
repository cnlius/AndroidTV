package com.ls.tv.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;

import com.ls.tv.R;
import com.ls.tv.model.Movie;
import com.ls.tv.presenter.CardPresenter;
import com.ls.tv.presenter.GridItemPresenter;
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
     * 1. 整个内容界面看做是一个rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());；
     * 2. ArrayObjectAdapter对象由实现了Presenter接口的对象构造；
     * 3. 实现了Presenter接口的类，决定了ArrayObjectAdapter的UI界面样式；
     * 4. rowsAdapter有可以填充左侧抽屉导航栏和右侧内容区域；
     * 5. rowsAdapter填充对象时一组ListRow;
     * 6. ListRow由HeaderItem(导航栏标题)和ArrayObjectAdapter(内容区)构成；
     */
    private void loadRows() {
        //主界面容器adapter：导航栏HeaderItem和rowsAdapter成对构成
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        //--第1组内容--------------------------------------------
        /* GridItemPresenter */
        //1->创建一个导航栏标题；
        HeaderItem gridItemPresenterHeader = new HeaderItem(0, "GridItemPresenter");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        //2->创建每个导航栏标题对应的内容
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("ITEM 1");
        gridRowAdapter.add("ITEM 2");
        gridRowAdapter.add("ITEM 3");
        mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));
        //--第2组内容--------------------------------------------
        /* CardPresenter */
        HeaderItem cardPresenterHeader = new HeaderItem(1, "CardPresenter");
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setTitle("title" + i);
            movie.setStudio("studio" + i);
            movie.setCardImageUrl("https://raw.githubusercontent.com/cnlius/resource/master/images/other/view_01.jpg");
            cardRowAdapter.add(movie);
        }
        mRowsAdapter.add(new ListRow(cardPresenterHeader, cardRowAdapter));

        /* set */
        setAdapter(mRowsAdapter);
    }

}
