package com.moxi.biji.intf;

/**
 * 处理事件同步事件回调
 * Created by Administrator on 2019/2/28.
 */
public interface BackImp {
    /**
     * 开始
     */
    void start();

    /**
     * 结束
     */
    void result();

    /**
     * 错误
     */
    void error(Exception e);
}
