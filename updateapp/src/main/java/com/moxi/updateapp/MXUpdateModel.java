package com.moxi.updateapp;

import java.io.Serializable;

/**
 * Created by zhengdelong on 2016/10/14.
 */

public class MXUpdateModel implements Serializable {

    private String appDesc;
    private String downloadUrl;
    private String packageName;
    private String versionName;
    private int versionCode;
    private int updateType;
    private String md5Str;
    private int isLancher;
    private String apkLocalPath;

    public String getApkLocalPath() {
        return apkLocalPath;
    }

    public void setApkLocalPath(String apkLocalPath) {
        this.apkLocalPath = apkLocalPath;
    }

    public int getIsLancher() {
        return isLancher;
    }

    public void setIsLancher(int isLancher) {
        this.isLancher = isLancher;
    }

    public String getMd5Str() {
        return md5Str;
    }

    public void setMd5Str(String md5Str) {
        this.md5Str = md5Str;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
