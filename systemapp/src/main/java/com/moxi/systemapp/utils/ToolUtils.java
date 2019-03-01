package com.moxi.systemapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import com.moxi.systemapp.listener.InstallListener;
import com.mx.mxbase.constant.APPLog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Created by xj on 2018/4/24.
 */

public class ToolUtils {
    /**
     * 获得系统根目录
     *
     * @return
     */
    public static String getSystemRoot() {
        return Environment.getRootDirectory().getPath();
    }

    /**
     * 静默安装应用
     *
     * @param filePath 安装apk路径
     */
    public static void install(final Handler handler, final String filePath, final InstallListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(filePath);
                if (filePath == null || filePath.length() == 0 || file == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) listener.onInstallResult(0, filePath);
                        }
                    });
                    return;
                }
                String[] args = {"pm", "install", "-r", filePath};
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                Process process = null;
                BufferedReader successResult = null;
                BufferedReader errorResult = null;
                StringBuilder successMsg = new StringBuilder();
                StringBuilder errorMsg = new StringBuilder();
                try {
                    process = processBuilder.start();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String s;
                    while ((s = successResult.readLine()) != null) {
                        successMsg.append(s);
                    }
                    while ((s = errorResult.readLine()) != null) {
                        errorMsg.append(s);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        if (successResult != null) {
                            successResult.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (process != null) {
                        process.destroy();
                    }
                }
                APPLog.d("successMsg", successMsg.toString());
                if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) listener.onInstallResult(1, filePath);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) listener.onInstallResult(2, filePath);
                        }
                    });
                }
            }
        }).start();
    }

    public static void uninstall(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            Method[] methods = pm != null ? pm.getClass().getDeclaredMethods() : null;
            Method mDel = null;
            if (methods != null && methods.length > 0) {
                for (Method method : methods) {
                    if (method.getName().toString().equals("deletePackage")) {
                        mDel = method;
                        break;
                    }
                }
            }
            if (mDel != null) {
                mDel.setAccessible(true);
                mDel.invoke(pm, packageName, null, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 安装apk */
    public static void installApk(Context context, String fileName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + fileName),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /* 卸载apk */
    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmd(String cmd) {
        String TAG="execRootCmd";
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            APPLog.d(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                APPLog.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
