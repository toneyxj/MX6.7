package com.moxi.systemapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.mx.mxbase.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by xj on 2018/4/27.
 */

public class UninstallApp  extends AsyncTask<String, Void, Integer> {
    private WeakReference<Context> context;
    //初始化文件处理类
    private  String packageName;//目标路径
    private  UnInstallListener listener;

    /**
     * 移动文件构造方法
     * @param context 当前上下文
     */
    public UninstallApp(Context context,String packageName,UnInstallListener listener) {
        this.packageName = packageName;
        this.context = new WeakReference<Context>(context);
        this.listener=listener;

    }

    @Override
    protected Integer doInBackground(String... params) {
        if (StringUtils.isNull(packageName))return 0;
        int is=0;
        try {
            PackageManager pm = context.get().getPackageManager();
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
                is=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    @Override
    protected void onPostExecute(Integer result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (listener!=null){
            listener.onUnInstall(result);
        }
    }

    public interface UnInstallListener{
        /**
         *
         * @param is 1成功，0失败
         */
        void onUnInstall(int is);
    }

}