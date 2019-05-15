package com.mx.mxbase.YuYinmanager;

/**
 * Created by xiajun on 2019/5/13.
 */

public interface YuYinCallBack {
    /**
     * 错误
     * @param e
     */
    void onYuYinFail(String e);

    /**
     * 完成
     */
    void onYuYinOver();

    /**
     * 开始
     */
    void onYuYinStart();
}
