package com.ls.tv.presenter;

import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ls.tv.R;

/**
 * 对应主界面左侧抽屉导航栏的标题组内的每个item的UI
 * presenter用来构建ArrayObjectAdapter
 * Created by liusong on 2018/1/11.
 */

public class GridItemPresenter extends Presenter  {
    /* Grid row item settings */
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(context.getResources().getColor(R.color.default_background));
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        return new Presenter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ((TextView) viewHolder.view).setText((String) item);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }
}
