package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.view.PassWordEditText;
import com.moxi.haierc.view.PassWordKeyboard;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Intent.ACTION_TIME_TICK;

public class OpenPasswordActivity extends Activity {
    private MyHandler handler;
    private int errorSize = 0;

    private void handleMessage(Message msg) {
        if (msg.what == 1) {
            shutScreen();
        }
    }

    private String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "删除", "0", "完成"
    };
    private TextView hitn;
    private TextView dianliang;
    private TextView shijian;
    private TextView rili;
    private PassWordEditText payEditText;
    private PassWordKeyboard keyboard;
    private String password = "";
    private long errorTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_password);
        if (savedInstanceState != null) {
            password = savedInstanceState.getString("password", "");
        } else {
            password = getIntent().getStringExtra("password");
        }
        if (StringUtils.isNull(password)) {
            finish();
        }
        errorTime = SharePreferceUtil.getInstance(MyApplication.applicationContext).getLong("errorTime");
        handler = new MyHandler(this);

        rili = (TextView) findViewById(R.id.rili);
        shijian = (TextView) findViewById(R.id.shijian);
        dianliang = (TextView) findViewById(R.id.dianliang);
        hitn = (TextView) findViewById(R.id.hitn);
        payEditText = (PassWordEditText) findViewById(R.id.psw_lock_device);
        keyboard = (PassWordKeyboard) findViewById(R.id.psw_lock__keyboard);
        keyboard.setKeyboardKeys(KEY);
        keyboard.setOnClickKeyboardListener(new PassWordKeyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (errorSize >= 5 || System.currentTimeMillis() - errorTime <= 300 * 1000) {
                    hitnValue("错误次数过多暂时无法输入");
                    return;
                }
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 9) {
                    payEditText.remove();
                } else if (position == 11) {
                    if (payEditText.getText().length() == 6) {
                        start();
                    } else {
                        hitnValue("请输入完整设备密码");
                    }
                    return;
                }
                if (payEditText.getText().length() == 6) {
                    start();
                }
            }
        });
        initView();
    }

    private void initView() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);

        initTime();
    }
    private void initTime(){
        //时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);
        String day = String.valueOf(cal.get(Calendar.DATE));
        String hour;
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        String minute = String.valueOf(cal.get(Calendar.MINUTE));

        rili.setText(year + "年" + month + "月" + day+"日");
        String timeValue=hour + ":" + minute ;
        shijian.setText(timeValue);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()==Intent.ACTION_BATTERY_CHANGED) {
                try {
                    int current = intent.getExtras().getInt("level");// 获得当前电量
                    int total = intent.getExtras().getInt("scale");// 获得总电量
                    int percent = current * 100 / total;
                    dianliang.setText("电量：" + percent + "%");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(intent.getAction()== ACTION_TIME_TICK) {

            }
        }
    };

    private void start() {
        String pas = payEditText.getText();
        if (password.equals(pas)) {
            sendBroadcast(new Intent(StringUtils.OPENPASSWORDBRODCAST));
            this.finish();
        } else {
            errorSize++;
            if (errorSize >= 5) {
                long time = System.currentTimeMillis();
                SharePreferceUtil.getInstance(MyApplication.applicationContext).setCache("errorTime", time);
                errorTime = time;
                errorSize = 0;
            }
            hitnValue("密码输入有误");
            payEditText.removeAll();
        }

    }

    private void hitnValue(String value) {
//        if (value.equals("")) {
//        hitn.setText("请输入设备密码");
//            return;
//        }
//        hitn.setText(value);
//        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//        hitn.startAnimation(shake);
        ToastUtils.getInstance().showToastShort(value);
    }

    private long timeSystem = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password", password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        payEditText.removeAll();
        sendHandler();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        sendHandler();
        return super.dispatchTouchEvent(ev);
    }

    private void sendHandler() {
        if (System.currentTimeMillis() - timeSystem > 20) {
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(1, 15000);
            timeSystem = System.currentTimeMillis();
        }
    }

    /**
     * 熄屏
     */
    private void shutScreen() {
        Intent intent = new Intent("com.moxi.systemapp.brodcast.SimulatorClick");
        intent.putExtra("type", 26);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private static class MyHandler extends Handler {

        WeakReference<OpenPasswordActivity> mReference = null;

        MyHandler(OpenPasswordActivity activity) {
            this.mReference = new WeakReference<OpenPasswordActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            OpenPasswordActivity outer = mReference.get();
            if (outer == null || outer.isFinishing()) {
                Log.e("outer is null");
                return;
            }
            outer.handleMessage(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}
