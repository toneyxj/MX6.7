package com.moxi.systemapp.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by xj on 2018/4/24.
 */

public class AppInfo {
    private String appLabel;    //应用程序标签
    private Drawable appIcon ;  //应用程序图像
    private Intent intent ;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
    private String pkgName ;    //应用程序所对应的包名
    private int flag;//0系统应用，1三方应用，2修改后的系统应用

    public AppInfo(String appLabel, Drawable appIcon, Intent intent, String pkgName, String activityName, int flag) {
        this.appLabel = appLabel;
        this.appIcon = appIcon;
        this.intent = intent;
        this.pkgName = pkgName;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


    public AppInfo(){}

    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public Intent getIntent() {
        return intent;
    }
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appLabel='" + appLabel + '\'' +
                ", appIcon=" + appIcon +
                ", intent=" + intent +
                ", pkgName='" + pkgName + '\'' +
                ", flag=" + flag +
                '}';
    }
}
