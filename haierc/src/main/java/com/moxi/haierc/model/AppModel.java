package com.moxi.haierc.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by King on 2017/10/10.
 */

public class AppModel extends DataSupport implements Serializable {
    private int id;
    private int isShow;//是否正在显示
    private int isCanReplace;//是否能被替换
    private int isSystem;//是否是系统应用
    private String appPackageName;//应用包名
    private String appLauncherClass;//应用启动类
    private String appName;//应用名
    private int position;//当前显示的位置0-7
    private long updateTime;//更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public int getIsCanReplace() {
        return isCanReplace;
    }

    public void setIsCanReplace(int isCanReplace) {
        this.isCanReplace = isCanReplace;
    }

    public int isSystem() {
        return isSystem;
    }

    public void setSystem(int system) {
        isSystem = system;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLauncherClass() {
        return appLauncherClass;
    }

    public void setAppLauncherClass(String appLauncherClass) {
        this.appLauncherClass = appLauncherClass;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
