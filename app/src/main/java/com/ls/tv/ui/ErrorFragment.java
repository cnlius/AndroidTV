package com.ls.tv.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ls.tv.R;


/**
 * 错误内容界面：ErrorFragment
 */
public class ErrorFragment extends android.support.v17.leanback.app.ErrorFragment {

    private static final String TAG = ErrorFragment.class.getSimpleName();
    private static final boolean TRANSLUCENT = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //设置标题
        setTitle(getResources().getString(R.string.app_name));
        //设置错误内容
        setErrorContent();
    }

    /**
     * 设置错误内容
     */
    public void setErrorContent() {
        //错误图片
        setImageDrawable(getActivity().getDrawable(R.drawable.lb_ic_sad_cloud));
        //错误信息
        setMessage(getResources().getString(R.string.error_fragment_message));
        //默认背景透明：false不透明
        setDefaultBackground(!TRANSLUCENT);

        //按钮
        setButtonText(getResources().getString(R.string.dismiss_error));
        setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(getActivity() instanceof BrowseErrorActivity){
                    getActivity().finish();
                }else{
                    getFragmentManager().beginTransaction().remove(ErrorFragment.this).commitAllowingStateLoss();
                }
            }
        });
    }
}