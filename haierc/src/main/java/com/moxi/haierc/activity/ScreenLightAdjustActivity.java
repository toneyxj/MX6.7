package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.view.ProgressView;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;

/**
 * 屏幕亮度调节
 */
public class ScreenLightAdjustActivity extends Activity implements View.OnClickListener, ProgressView.ProgressListener {
    private TextView current_pen_size;
    private ImageView sub;
    private ProgressView current_progress;
    private ImageView add;
    Integer intValues[] = {0,
            80,90,100,110,120,130,140,150,160,170,
            180,190,200,210,218,226,234,242,246,250};

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                ScreenLightAdjustActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_light_adjust);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity( Gravity.TOP);
        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth);

        current_pen_size = (TextView) findViewById(R.id.current_pen_size);
        sub = (ImageView) findViewById(R.id.sub);
        current_progress = (ProgressView) findViewById(R.id.current_progress);
        add = (ImageView) findViewById(R.id.add);

        sub.setOnClickListener(this);
        add.setOnClickListener(this);
        current_progress.initView(this, intValues.length, gecurrentIndex());

        sendHander();

    }

    @Override
    public void onClick(View v) {
        sendHander();
        switch (v.getId()) {
            case R.id.add:
                current_progress.subOrAdd(true);
                break;
            case R.id.sub:
                current_progress.subOrAdd(false);
                break;
            default:
                break;
        }
    }

    private void sendHander() {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(100, 5000);
    }

    @Override
    public void onProgress(int size) {
        sendHander();
        current_pen_size.setText("当前亮度：" + size);
        if (size >= intValues.length) size = intValues.length - 1;
        saveBrightness(this, size);
    }

    private int gecurrentIndex() {
        int now = getScreenBrightness(this);
        APPLog.e("gecurrentIndex-now", now);
        int index = 0;
        int size = intValues.length;
        if (now < 250) {
            for (int i = 0; i < size; i++) {
                if (now == intValues[i]) {
                    index = i;
                    break;
                } else if (i < (size - 1) && now < intValues[i + 1] && now > intValues[i]) {
                    index = i;
                    break;
                } else {
                    index = size-1;
                }
            }
        } else {
            index = size-1;
        }
        APPLog.e("index",index);
        current_pen_size.setText("当前亮度：" + index);
        return index;
    }

    /**
     * 获取屏幕的亮度
     */
    private int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
//        }
        try {
            nowBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 保存亮度设置状态，退出app也能保持设置状态
     */
    private void saveBrightness(Context context, int brightness) {
        if (brightness < 0) brightness = 0;
        if (brightness >= (intValues.length - 1)) brightness = intValues.length - 1;
        APPLog.e("当前亮度值", intValues[brightness]);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, intValues[brightness]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
