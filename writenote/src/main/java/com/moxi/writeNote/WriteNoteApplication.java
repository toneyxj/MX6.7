package com.moxi.writeNote;

import com.moxi.handwritinglibs.db.index.IndexDbUtils;
import com.moxi.writeNote.utils.UserInformation;
import com.mx.mxbase.base.MyApplication;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/2/16.
 */
public class WriteNoteApplication extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "4bd918ae08", false);
        IndexDbUtils.getInstance().initDb(getApplicationContext());
        UserInformation.getInstance().initUserInfor(getInstance());
    }
}
