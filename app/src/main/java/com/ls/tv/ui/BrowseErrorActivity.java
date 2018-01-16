package com.ls.tv.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.ls.tv.R;

/**
 * 错误页面:
 * 1> 可以跳转到新的页面；
 * 2> 在当前页面上覆盖一层错误页面；
 * Created by liusong on 2018/1/16.
 */

public class BrowseErrorActivity extends Activity {
    private ErrorFragment mErrorFragment;
    private SpinnerFragment mSpinnerFragment;
    private static int TIMER_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //临时测试
        setContentView(R.layout.activity_error);

        testError();
    }

    /**
     * 一个error页面
     */
    private void testError() {
        mErrorFragment = new ErrorFragment();
        getFragmentManager().beginTransaction().add(R.id.container_error, mErrorFragment).commit();

        mSpinnerFragment = new SpinnerFragment();
        getFragmentManager().beginTransaction().add(R.id.container_error, mSpinnerFragment).commit();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
                mErrorFragment.setErrorContent();
            }
        }, TIMER_DELAY);
    }
}
