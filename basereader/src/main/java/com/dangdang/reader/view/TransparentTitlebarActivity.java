package com.dangdang.reader.view;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.zframework.BaseActivity;
import com.dangdang.zframework.utils.SystemBarTintManager;

abstract public class TransparentTitlebarActivity extends BaseReaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitlebar();
    }
    void initTitlebar(){
        if(Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.title_bg));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
}
