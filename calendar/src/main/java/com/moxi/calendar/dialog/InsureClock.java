package com.moxi.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.moxi.calendar.utils.TimerUtils;
import com.mx.mxbase.base.BaseApplication;

/**
 * Created by Administrator on 2016/9/9.
 */
public class InsureClock extends Dialog implements View.OnClickListener{
    private TextView contnet;// dialog显示文字控件
    private TextView remark;// dialog显示文字控件
    private Button insure;// 取消
    private String content;// 标题
    private String hint;// 提示内容
    private  ClockListener listener;
    TimerUtils timerUtils=null;

    public InsureClock(Context context, int theme, String content, String hint,
                       ClockListener listener) {
        super(context, theme);
        this.hint = hint;
        this.content = content;
        this.listener=listener;
    }

        private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_clock);
        //开启提示闹钟

        remark = (TextView) findViewById(R.id.remark);
        contnet = (TextView) findViewById(R.id.contnet);
        insure = (Button) findViewById(R.id.insure);

        remark.append(hint);
        contnet.append(content);

        mediaPlayer=StartMsgVoice(true);

        insure.setOnClickListener(this);

        timerUtils=new TimerUtils(TimerUtils.TimerE.DOWN, 50, 0, new TimerUtils.TimeListener() {
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


    public static void getdialog(Context context,String title,String hint,
                                 ClockListener listener) {
        InsureClock dialog = new InsureClock(context, R.style.dialog,title,
                hint, listener);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        window.getDecorView().setPadding(BaseApplication.ScreenWidth / 6, 0, BaseApplication.ScreenWidth / 6, 0);
        window.setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
      dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.quit();
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
    /**
     * 自定义提示声
     */
    private MediaPlayer StartMsgVoice(boolean forever){
        MediaPlayer mPlayer = MediaPlayer.create(getContext(), R.raw.msg);
        mPlayer.setLooping(forever);
        mPlayer.start();
        return mPlayer;
    }

    public interface  ClockListener{
        public void quit();
    }
}
