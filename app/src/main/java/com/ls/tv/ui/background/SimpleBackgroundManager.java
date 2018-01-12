package com.ls.tv.ui.background;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;

import com.ls.tv.R;


/**
 * 处理内容区实际的背景更改
 */
public class SimpleBackgroundManager {
    private final int DEFAULT_BACKGROUND_RES_ID = R.drawable.default_background;
    private static Drawable mDefaultBackground;

    private Activity mActivity;
    private BackgroundManager mBackgroundManager;

    public SimpleBackgroundManager(Activity activity) {
        mActivity = activity;
        //默认背景图
        mDefaultBackground = activity.getDrawable(DEFAULT_BACKGROUND_RES_ID);
        //BackgroundManager单例实例
        mBackgroundManager = BackgroundManager.getInstance(activity);
        //-------------------------------------------------------
        //在更新背景之前必须附加到Window
        mBackgroundManager.attach(activity.getWindow());
        //-------------------------------------------------------
        //获取到Activity的实际屏幕信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
    }

    /**
     * 改变背景
     * @param drawable
     */
    public void updateBackground(Drawable drawable) {
        mBackgroundManager.setDrawable(drawable);
    }

    /**
     * 还原默认图像背景
     */
    public void clearBackground() {
        mBackgroundManager.setDrawable(mDefaultBackground);
    }

}