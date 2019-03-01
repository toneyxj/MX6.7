package com.moxi.systemapp.listener;

/**
 * Created by xj on 2018/4/27.
 */

public interface InstallListener {
    /**
     * 暗转监听
     * @param code 返回标志，1安装成功，其它安装失败
     * @param filePath
     */
    void onInstallResult(int code,String filePath);
}
