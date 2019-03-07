package com.moxi.bookstore;


import android.content.Context;

import com.dangdang.reader.DDApplication;
import com.moxi.bookstore.utils.Installation;
import com.mx.mxbase.constant.APPLog;
import com.tencent.bugly.crashreport.CrashReport;


/**
 * Created by Administrator on 2016/9/12.
 */
public class BookstoreApplication extends DDApplication {

    private static BookstoreApplication mApplication;
    private String pub,pri,stuFlag;



    /**
     * 获取Context
     * @return 返回Context的对象
     */
    public static Context getContext(){
//        if (mApplication==null)return null;
        return mApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mApplication=this;
        initMyApp();
//        CrashHandler.getInstance().init(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext(), "4bd918ae08", APPLog.is);
//        MultiDex.install(this);
//        CrashHandler.getInstance().init(getContext());
    }
    public void setStuFlag(String flag){
        stuFlag=flag;
    }
    public String getStuFlag(){
        return stuFlag;
    }
    private void initMyApp(){
        super.onCreateIpml();
    }

    public static String getDeviceNO(){
        return Installation.id(mApplication);
        //return ToolUtils.getIntence().getIMEINo(mApplication);
        //return ToolUtils.getIntence().getDeviceNo(mApplication);
    }


}
