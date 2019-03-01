package com.moxi.haierc.util;

import com.moxi.haierc.ports.FinishCallBack;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xj on 2018/9/4.
 */

public class RootCmd {
    private static final String TAG = "RootCmd";
    public static  final String hwtool=StringUtils.getSDPath()+"/hwtool";
    public static  final String firmware_name=StringUtils.getSDPath()+"/firmware_name.bin";

    /**
     * 获取当前屏幕版本号
     */
    public static String gethwtV() {
        try {
            String command = "hwtools -v";
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);

            InputStream in = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String msg = null;
            String value="";
            while ((msg = reader.readLine()) != null) {
                APPLog.e("gethwtV",msg);
                if (msg.contains("version_s")){
                    msg=msg.replace("version_s","");
                    msg=msg.replace("=","");
                    value=msg.trim();
                    break;
                }
            }
            in.close();
            return value;
        } catch (IOException e) {//没有文件的情况是报错
            e.printStackTrace();
            APPLog.e(TAG+"-gethwtV", e.getMessage());
        }
        return "";
    }
    /**
     * 修改文件权限
     * @param tagPath
     */
    public static void updateChomd(String tagPath) {
        try {
            String command = "chmod 777 " + tagPath;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            APPLog.e(TAG+"-updateChomd", e.getMessage());
        }
    }

    /**
     * 升级文件
     * @param callBack 异步线程回调
     */
    public static void updateFirmware(final String name, final FinishCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean is=false;
                try {
                    String command = "hwtools update "+name;
                    Runtime runtime = Runtime.getRuntime();
                    Process proc = runtime.exec(command);

                    InputStream in = proc.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String msg = null;
                    while ((msg = reader.readLine()) != null) {
                        String ok="update firmware ok!";
                        if (msg.contains(ok)){
                            is=true;
                        }
                        APPLog.e("updateFirmware",msg);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    APPLog.e(TAG+"-updateFirmware", e.getMessage());
                }finally {
                    if (callBack!=null){
                        callBack.onFinish(is);
                    }
                }
            }
        }).start();
    }

}
