package com.moxi.calendar;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.calendar.utils.TimerUtils;
import com.mx.mxbase.base.BaseActivity;

public class AlarmClockActivity extends BaseActivity implements View.OnClickListener{
    private TextView contnet;// dialog显示文字控件
    private TextView remark;// dialog显示文字控件
    private Button insure;// 取消
    private String contentStr;// 标题
    private String hint;// 提示内容
    TimerUtils timerUtils=null;
    private MediaPlayer mediaPlayer;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_alarm_clock;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //下面就是根据自己的跟你需求来写，跟写一个Activity一样的
        //拿到传过来的数据
        contentStr = getIntent().getStringExtra("content");
         hint = getIntent().getStringExtra("hint");

        //开启提示闹钟

        remark = (TextView) findViewById(R.id.remark);
        contnet = (TextView) findViewById(R.id.contnet_value);
        insure = (Button) findViewById(R.id.insure);

        remark.append(hint);
        contnet.append(contentStr);

        mediaPlayer=StartMsgVoice(true);

        insure.setOnClickListener(this);

        timerUtils=new TimerUtils(TimerUtils.TimerE.DOWN, 60, 0, new TimerUtils.TimeListener() {
            @Override
            public void cuttentTime(int time) {

            }

            @Override
            public void TimeEnd() {
                if (mediaPlayer!=null&&mediaPlayer.isLooping()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                if (timerUtils!=null){
                    timerUtils.stopTimer();
                    timerUtils=null;
                }
            }
        });
        timerUtils.startTimer();
    }



    /**
     * 自定义提示声
     */
    private MediaPlayer StartMsgVoice(boolean forever){
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.msg);
        mPlayer.setLooping(forever);
        mPlayer.start();
        return mPlayer;
    }
    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null&&mediaPlayer.isLooping()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if (timerUtils!=null){
            timerUtils.stopTimer();
            timerUtils=null;
        }
        this.finish();
    }
}
