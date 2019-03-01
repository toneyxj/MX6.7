package com.moxi.systemapp.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.constant.APPLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

/**
 * Created by xj on 2018/4/27.
 */

public class InstallApp  extends AsyncTask<String, Void, Integer> {
    private WeakReference<Context> context;
    //初始化文件处理类
    private  String filePath;//目标路径
    private InstallListener listener;

    /**
     * 移动文件构造方法
     * @param context 当前上下文
     */
    public InstallApp(Context context,String filePath,InstallListener listener) {
        this.filePath = filePath;
        this.context = new WeakReference<Context>(context);
        this.listener=listener;

    }

    @Override
    protected Integer doInBackground(String... params) {
        int is=0;
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0 || file == null) {
            return is;
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
        APPLog.e("successMsg", successMsg.toString());
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            is=1;
        } else {
            is=2;
        }
        return is;
    }

    @Override
    protected void onPostExecute(Integer result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (listener!=null){
            listener.onInstall(result);
        }
    }

    public interface InstallListener{
        /**
         *
         * @param is 1成功，其它失败
         */
        void onInstall(int is);
    }

}