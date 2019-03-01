package com.moxi.systemapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by xj on 2018/4/27.
 */

public class AppReceiver  extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        APPLog.d(intent.getAction());
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            APPLog.e(TAG, "--------安装成功" + packageName);
            Intent inintent = new Intent();
            //设置与动态相同的Action，方便同时触发静态与动态
            inintent.setAction("installSucess");
            context.sendBroadcast(inintent);//默认广播
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            APPLog.e(TAG, "--------替换成功" + packageName);

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            APPLog.e(TAG, "--------卸载成功" + packageName);
            Intent unintent = new Intent();
            //设置与动态相同的Action，方便同时触发静态与动态
            unintent.setAction("installSucess");
            context.sendBroadcast(unintent);//默认广播
        }
    }

}