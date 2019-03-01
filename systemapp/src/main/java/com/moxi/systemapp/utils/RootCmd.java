package com.moxi.systemapp.utils;

import com.moxi.systemapp.listener.AdbPathListener;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private static boolean mHaveRoot = false;

    /**
     * 获取当前屏幕版本号
     */
    public static void gethwtV() {
        try {
            String command = "hwtool -v";
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);

            InputStream in = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line1=reader.readLine();
            APPLog.e("gethwtV-line1",line1);
            in.close();
        } catch (IOException e) {//没有文件的情况是报错
            e.printStackTrace();
            APPLog.e(TAG+"-gethwtV", e.getMessage());
        }
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
     */
    public static void updateFirmware(String name) {
        //firmware_name.bin
        try {
            String command = "hwtools update /data/"+name;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            APPLog.e(TAG+"-updateFirmware", e.getMessage());
        }
    }
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(final String oldPath, final String newPath, final boolean upChom, final AdbPathListener finishCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int byteread = 0;
                    File oldfile = new File(oldPath);
                    if (oldfile.exists()) { //文件存在时
                        InputStream inStream = new FileInputStream(oldPath); //读入原文件
                        FileOutputStream fs = new FileOutputStream(newPath);
                        byte[] buffer = new byte[2048];
                        while ((byteread = inStream.read(buffer)) != -1) {
                            fs.write(buffer, 0, byteread);
                        }
                        fs.close();
                        inStream.close();
                        if (upChom)
                        updateChomd(newPath);

                        finishCallBack.onFinish(true,newPath);
                    }else {
                        finishCallBack.onFinish(false,newPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    StringUtils.deleteFile(newPath);
                    finishCallBack.onFinish(false,newPath);
                } finally {
                    StringUtils.deleteFile(oldPath);
                }
            }
        }).start();
    }

    public static void startUpF(AdbPathListener listener){
        File hwt=new File(hwtool);
        if (!hwt.exists()||!hwt.canRead()) {
            StringUtils.deleteFile(hwt);
            return;
        }
        File firn=new File(firmware_name);
        if (!firn.exists()||!firn.canRead()) {
            StringUtils.deleteFile(firn);
            return;
        }
        String oneTag="system/bin/hwtool";
        String twoTag="data/firmware_name.bin";

        copyFile(hwtool,oneTag,true,listener);
        copyFile(firmware_name,twoTag,false,listener);
    }
    private static AdbPathListener listener=new AdbPathListener() {
        @Override
        public void onFinish(boolean is, String tag) {
            APPLog.e("AdbPathListener-onFinish"+is,tag);
        }
    };
    public static void deleteFile(){
        StringUtils.deleteFile(hwtool);
        StringUtils.deleteFile(firmware_name);
    }
}
