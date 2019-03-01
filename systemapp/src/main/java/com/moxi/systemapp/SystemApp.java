package com.moxi.systemapp;

import com.liulishuo.filedownloader.FileDownloader;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by xj on 2018/4/24.
 */

public class SystemApp extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "21321eaa58", APPLog.is);
        FileDownloader.setupOnApplicationOnCreate(this);

    }
}
