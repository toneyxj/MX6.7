package com.dangdang.reader;

import android.app.ActivityManager;
import android.os.*;

import com.dangdang.zframework.plugin.AppUtil;
import com.mx.mxbase.base.MyApplication;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public abstract class DDBaseApplication extends MyApplication{
    protected boolean isBDService = false;

    public DDBaseApplication() {
    }

    public void onCreate() {
        super.onCreate();
        int myId = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)this.getSystemService("activity");
        List runningAppProcesses = activityManager.getRunningAppProcesses();
        Iterator var5 = runningAppProcesses.iterator();

        while(var5.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)var5.next();
            if(info.pid == myId) {
                if(info.processName != null && info.processName.contains("bdservice_v1")) {
                    this.isBDService = true;
                }
                break;
            }
        }

        if(!this.isBDService) {
            AppUtil.getInstance(this.getApplicationContext());
            this.onCreateIpml();
        }
    }

    public abstract void onCreateIpml();
}
