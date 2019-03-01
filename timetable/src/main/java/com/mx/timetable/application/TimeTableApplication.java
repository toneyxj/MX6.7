package com.mx.timetable.application;

import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.constant.APPLog;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Archer on 16/8/31.
 */
public class TimeTableApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "c2a6e2d216", APPLog.is);
    }
}
