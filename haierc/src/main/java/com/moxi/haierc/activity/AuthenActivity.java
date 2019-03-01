package com.moxi.haierc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierc.R;

/**
 * Created by King on 2017/6/15.
 */

public class AuthenActivity extends Activity implements View.OnClickListener {

    TextView tv_base_mid_title;
    TextView tv_base_back;
    LinearLayout ll_base_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_authen);

        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        tv_base_back.setText("认证信息");
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setVisibility(View.VISIBLE);
        ll_base_back.setOnClickListener(this);
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        tv_base_mid_title.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_base_back) {
            this.finish();
        } else {
        }
    }
}
