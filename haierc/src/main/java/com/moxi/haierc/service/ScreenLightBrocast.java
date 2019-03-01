package com.moxi.haierc.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;

import com.moxi.haierc.activity.ScreenLightAdjustActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.util.List;

/**
 * Created by xj on 2018/7/20.
 */

public class ScreenLightBrocast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(StartActivityUtils.brodcastLight))return;
        try {
            String name = intent.getStringExtra("name");
            if (name.equals("backlight")){
                Intent inLi=new Intent(context, ScreenLightAdjustActivity.class);
                inLi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(inLi);
            }else if (name.equals("clearapp")){
                if (clearIng) return;
                clearIng = true;
                try {
                    killAll(context);
                } catch (Exception e) {
                    APPLog.e(TAG, e.getMessage());
                } finally {
                    clearIng = false;
                }
            }
        }catch (Exception e) {
            APPLog.e("ScreenLightBrocast",e.getMessage());
        }
    }


    private String TAG = "ClearApp";
    private static boolean clearIng = false;
    /*     * 杀死后台进程     */
    public void killAll(Context context) {

        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName runningTopActivity = activityManager.getRunningTasks(1).get(0).topActivity;
        APPLog.e(TAG+"runningTopActivity",runningTopActivity.getPackageName());
        //获取系统中所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
//        int count = 0;//被杀进程计数
        String nameList = "";
        //记录被杀死进程的包名
        long beforeMem = getAvailMemory(context);
//         清理前的可用内存

        APPLog.e(TAG, "清理前可用内存为 : " + beforeMem);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            nameList = "";
            APPLog.e(TAG+"appProcessInfo.processName", appProcessInfo.processName);
            if (appProcessInfo.processName.contains("com.android.system")
                    || appProcessInfo.pid == android.os.Process.myPid()
                    ||runningTopActivity.getClassName().equals(appProcessInfo.processName)
                    ||appProcessInfo.processName.contains("com.moxi.calendar")
                    ||appProcessInfo.processName.contains("com.moxi.haierc"))
                //跳过系统 及当前进程
                continue;
            String[] pkNameList = appProcessInfo.pkgList;
            //进程下的所有包名
            for (int i = 0; i < pkNameList.length; i++) {
                String pkName = pkNameList[i];
                activityManager.killBackgroundProcesses(pkName);//杀死该进程
                APPLog.e(TAG+"ClearPro",pkName);
//                count++;
                //杀死进程的计数+1
                nameList += "  " + pkName;
            }
            APPLog.e(TAG, nameList + "---------------------");
        }
        long afterMem = getAvailMemory(context);//清理后的内存占用
        long clearCount=afterMem - beforeMem;
        if (clearCount<=100){
            ToastUtils.getInstance().showToastShort("已达最佳状态");
        }else {
            ToastUtils.getInstance().showToastShort("释放" + formatFileSize(context, clearCount) + "内存");
        }

        APPLog.e(TAG, "清理后可用内存为 : " + afterMem);
//        APPLog.e(TAG, "清理进程数量为 : " + count + 1);
    }       /*   * *获取可用内存大小   */

    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }        /*     * *字符串转换 long-string KB/MB     */

    private String formatFileSize(Context context, long number) {
        return Formatter.formatFileSize(context, number);
    }
}
