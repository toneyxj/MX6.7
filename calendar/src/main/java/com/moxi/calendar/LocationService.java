package com.moxi.calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.moxi.calendar.model.EventBeen;
import com.moxi.calendar.utils.TimeChangeUtils;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地服务
 * Created by Administrator on 2016/9/9.
 */
public class LocationService extends Service{
    private static final String ACTION_TIME_TICK = Intent.ACTION_TIME_TICK;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time=System.currentTimeMillis();
            String times= TimeChangeUtils.getTime(time);
            APPLog.e("当前时间=" + times);
            Cursor cursor = DataSupport.findBySQL("select " +MainActivity.sqlSlect + " from EventBeen where notifyTime='" + times + "' order by saveTime ASC");
            List<EventBeen> listEvents=new ArrayList<>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String saveDate = cursor.getString(1);
                String saveTime = cursor.getString(2);
                String name = cursor.getString(3);
                String whetherNotify = cursor.getString(4);
                String setNotify = cursor.getString(5);
                String notifyTime = cursor.getString(6);
                String remark = cursor.getString(7);
                EventBeen eventBeen = new EventBeen(id, saveDate, saveTime, name, whetherNotify, setNotify, notifyTime, remark);
                listEvents.add(eventBeen);
            }
            if (listEvents.size()!=0){
                List<String> contents=new ArrayList<>();
                List<String> remakers=new ArrayList<>();

                for (int i = 0; i < listEvents.size(); i++) {
                    if (!listEvents.get(i).isDiabolo) {
                        ContentValues cv = new ContentValues();
                        cv.put("isDiabolo", true);
                        DataSupport.update(EventBeen.class, cv, listEvents.get(i).id);
                        if (!listEvents.get(i).whetherNotify.equals("0")) {
                            remakers.add(listEvents.get(i).remark);
                            contents.add(listEvents.get(i).name);
                        }
                    }
                }

                if (contents.size()!=0){
                    String contnet="";
                    for (int i = 0; i < contents.size(); i++) {
                        if (i==0) {
                            contnet += contents.get(i);
                        }else{
                            contnet +=","+contents.get(i);
                        }

                    }
                    String remaker="";
                    for (int i = 0; i < remakers.size(); i++) {
                        if (i==0) {
                            remaker += remakers.get(i);
                        }else{
                            remaker +=","+remakers.get(i);
                        }

                    }
//                    Intent intentss=new Intent(context,ClockActivity.class);
//                            intentss.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intentss);
//                    StartActivityUtils.sendSimulatorClick(context,3);
//                    wakeUpAndUnlock();
//                    InsureClock.getdialog(context, contnet, remaker, new InsureClock.ClockListener() {
//                        @Override
//                        public void quit() {
//
//                        }
//                    });
                    Intent intents = new Intent();
                    intents.setAction("com.moxi.calendar.NotifyClockRecever");
                    intents.putExtra("content", contnet);
                    intents.putExtra("hint",remaker );
                    LocationService.this.sendBroadcast(intents);
                }

            }
        }

    };
    /**
     * 唤醒手机屏幕并解锁
     */
    private   void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) MyApplication.getContext()
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
//        KeyguardManager keyguardManager = (KeyguardManager) MyApplication.getContext()
//                .getSystemService(KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
//        // 屏幕锁定
//        keyguardLock.reenableKeyguard();
//        keyguardLock.disableKeyguard(); // 解锁
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(ACTION_TIME_TICK);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        Intent intent = new Intent("com.moxi.calendar.destroy");
        sendBroadcast(intent);
        super.onDestroy();
    }
}
