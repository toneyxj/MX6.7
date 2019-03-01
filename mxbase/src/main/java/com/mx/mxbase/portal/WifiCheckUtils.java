package com.mx.mxbase.portal;

import android.content.Context;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ActivityUtils;

/**
 * Created by xj on 2018/8/22.
 */

public class WifiCheckUtils {
    /** wifi 认证 检测 **/
    public static void portalWifi(final Context context) {
        CheckWifiLoginTask.checkWifi(new CheckWifiLoginTask.ICheckWifiCallBack() {
            @Override
            public void portalNetWork(int isLogin) {
                APPLog.e("portalNetWork isLogin",isLogin);
//                if (isLogin==0){
                    if (ActivityUtils.isContextExisted(context)) {
//                        TestActivity.startWeb(context);
                    }
//                }
            }
        });
    }
}
