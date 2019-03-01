package com.moxi.systemapp.ok;

/**
 * Created by xj on 2018/8/7.
 */

public interface DownloadListener {


    /**
     * 通知当前的下载进度
     * @param progress
     */
    void onProgress(int progress, long total, long hasDownlaod);

    /**
     * 通知下载成功
     */
    void onSuccess();

    /**
     * 通知下载失败
     */
    void onFailed(Exception e);

    /**
     * 通知下载暂停
     */
    void onPaused();

    /**
     * 通知下载取消事件
     */
    void onCanceled();

}