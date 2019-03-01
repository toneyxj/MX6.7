package com.moxi.calendar.brodCast;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.moxi.calendar.AlarmClockActivity;
import com.mx.mxbase.constant.APPLog;

/**
 * Created by xj on 2018/8/1.
 */

public class NotifyClockRecever extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        //拿到传来过来数据
        String contnet = intent.getStringExtra("content");
        String hint = intent.getStringExtra("hint");
        APPLog.e("content",contnet);
        APPLog.e("hint",hint);
        //拿到锁屏管理者
//        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        if (km.isKeyguardLocked()) {   //为true就是锁屏状态下
//            APPLog.e("进入熄屏唤醒");
////            StartActivityUtils.sendSimulatorClick(context,3);
//        }
            //启动Activity
            Intent alarmIntent = new Intent(context, AlarmClockActivity.class);
            //携带数据
            alarmIntent.putExtra("content", contnet);
            alarmIntent.putExtra("hint", hint);
            //activity需要新的任务栈
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
//        }
    }
}
