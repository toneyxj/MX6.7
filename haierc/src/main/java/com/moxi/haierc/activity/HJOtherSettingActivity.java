package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierc.R;

import java.util.Calendar;

/**
 * Created by zhengdelong on 2016/11/15.
 */

public class HJOtherSettingActivity extends Activity implements View.OnClickListener {

    private long lastClickTime = 0;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";

    LinearLayout ll_base_back;
    int times = 0;

    TextView tv_base_mid_title;
    TextView tv_base_back;

    //电源管理
    RelativeLayout yinsi_rel;
    //应用设置
    RelativeLayout kuozhan2_rel, rlBeifen;
    //VCOM
    RelativeLayout rlBlueTooth;
    //关于设备
    TextView about_shebei;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hj_other_activity_setting);
        initView();
    }

    private void initView() {
        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        tv_base_back.setText("其他");
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setVisibility(View.VISIBLE);
        ll_base_back.setOnClickListener(this);
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        tv_base_mid_title.setVisibility(View.GONE);
        yinsi_rel = (RelativeLayout) findViewById(R.id.yinsi_rel);
        kuozhan2_rel = (RelativeLayout) findViewById(R.id.kuozhan2_rel);
        kuozhan2_rel.setVisibility(View.GONE);
        about_shebei = (TextView) findViewById(R.id.about_shebei);
        rlBlueTooth = (RelativeLayout) findViewById(R.id.bluetooth_rel);
        rlBeifen = (RelativeLayout) findViewById(R.id.rl_yingsi_tv);
        rlBeifen.setVisibility(View.GONE);

        yinsi_rel.setOnClickListener(this);
        kuozhan2_rel.setOnClickListener(this);
        about_shebei.setOnClickListener(this);
        rlBlueTooth.setOnClickListener(this);
        rlBeifen.setOnClickListener(this);
        findViewById(R.id.system).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.yinsi_rel) {
            //电源管理
            Intent intent = new Intent();
            intent.setClassName(SETTINGS_PACKAGE_NAME, "com.android.settings.DisplaySettings");
            startActivity(intent);
        } else if (v.getId() == R.id.yingsi_tv) {
            Intent intent = new Intent();
            intent.setClassName(SETTINGS_PACKAGE_NAME, "com.android.settings.PrivacySettings");
            startActivity(intent);
        } else if (v.getId() == R.id.kuozhan2_rel) {
            Intent intent = new Intent();
            intent.setClassName(SETTINGS_PACKAGE_NAME, "com.android.settings.ApplicationSettings");
            startActivity(intent);
        } else if (v.getId() == R.id.rl_yingsi_tv) {
            Intent intent = new Intent();
            intent.setClassName(SETTINGS_PACKAGE_NAME, "com.android.settings.PrivacySettings");
            startActivity(intent);
        } else if (v.getId() == R.id.about_shebei) {
            Intent intent = new Intent();
            intent.setClassName(SETTINGS_PACKAGE_NAME, "com.android.settings.DeviceInfoSettings");
            startActivity(intent);
        } else if (v.getId() == R.id.ll_base_back) {
            HJOtherSettingActivity.this.finish();
        } else if (v.getId() == R.id.system) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME || lastClickTime == 0) {
                times++;
                if (times == 5) {
                    rlBeifen.setVisibility(View.VISIBLE);
                    kuozhan2_rel.setVisibility(View.VISIBLE);
                }
            } else {
                times = 0;
            }
            lastClickTime = currentTime;
        } else if (v.getId() == R.id.bluetooth_rel) {
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        } else if (v.getId() == R.id.about_renzheng) {
            Intent intentAuthen = new Intent();
            intentAuthen.setClass(this, AuthenActivity.class);
            startActivity(intentAuthen);
        }
    }
}
