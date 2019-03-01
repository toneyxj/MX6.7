package com.moxi.calendar;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by Administrator on 2016/9/9.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        APPLog.e("BootReceiver",String.valueOf(intent.getAction()));
//        if (intent.getAction().equals("com.moxi.calendar.destroy")) {
            //在这里写重新启动service的相关操作
            //注册后台下载服务
            Intent i = new Intent(context, LocationService.class);
            context.startService(i);
//        }else{
//            startService(context);
//        }
    }
    private void startService(Context context){
        boolean isServiceRunning = false;
        //检查Service状态
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            APPLog.e(service.service.getClassName());
            if ("com.calendar.LocationService".equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        if (!isServiceRunning) {
            APPLog.e("是否需要重启服务", isServiceRunning);
            Intent i = new Intent(context, LocationService.class);
            context.startService(i);
        }
    }
}
