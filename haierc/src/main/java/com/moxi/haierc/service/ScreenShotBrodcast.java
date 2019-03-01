package com.moxi.haierc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.moxi.haierc.activity.ScreenShotPicActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

import xj.systemfunc.Tools;

/**
 * Created by xj on 2018/8/16.
 */

public class ScreenShotBrodcast extends BroadcastReceiver {
    private Handler handler=new Handler();
    private int index=0;
    private static String lastPath="";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(StartActivityUtils.screenShot)||StartActivityUtils.screenShot_BookSend.equals(intent.getAction())) {
            try {
                String backImgPath = intent.getStringExtra("backImgPath");
                if (lastPath.equals(backImgPath)) {
                    Log.i("ScreenShotBrodcast","Screenshot broadcast sent");
                    return;
                }
                if (StringUtils.isNull(backImgPath))return;
                lastPath=backImgPath;
                String title = intent.getStringExtra("title");
                boolean is=intent.getBooleanExtra("is_system_broadcast",false);
                APPLog.d("is_system_broadcast",is);
                APPLog.d("ScreenShotBrodcast-backImgPath", backImgPath);
                APPLog.d("ScreenShotBrodcast-title", title);
                if (StringUtils.isNull(title)) title="截屏";
                startA(context, backImgPath, title);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void startA(final Context context, final String backImgPath, final String title){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File file=new File(backImgPath);
                if (file.length()<=0){
                    if (index<=30)
                        if (!Tools.ishandwritestate())
                        ToastUtils.getInstance().showToastShort("截屏保存中...");
                    if (index>=200){
                        if (!Tools.ishandwritestate())
                        ToastUtils.getInstance().showToastShort("截屏失败！");
                        return;
                    }else {
                        index++;
                        startA(context, backImgPath, title);
                    }
                }else {
                    if (!Tools.ishandwritestate()) {
                        Intent inLi = new Intent(context, ScreenShotPicActivity.class);
                        inLi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putString("backImgPath", backImgPath);
                        bundle.putString("title", title);
                        inLi.putExtras(bundle);
                        context.startActivity(inLi);
                        index = 0;
                    }else {
                        //删除文件
                        StringUtils.deleteFile(backImgPath);
                    }
                }
            }
        },20);
    }
}
