package com.dangdang.reader.domain;

import java.io.Serializable;

/**
 * Created by liupan on 2015/8/6.
 * 限免信息 vo
 */
public class TimeFreeInfo implements Serializable {
    private int isFull;//是否全本
    private int isSupportFullBuy;//是否支持全本购买
    private int isTimeFree;//是否限免
    private int lastIndexOrder;//最新章节数

    public int getIsFull() {
        return isFull;
    }

    public void setIsFull(int isFull) {
        this.isFull = isFull;
    }

    public int getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(int isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public int getIsTimeFree() {
        return isTimeFree;
    }

    public void setIsTimeFree(int isTimeFree) {
        this.isTimeFree = isTimeFree;
    }

    public int getLastIndexOrder() {
        return lastIndexOrder;
    }

    public void setLastIndexOrder(int lastIndexOrder) {
        this.lastIndexOrder = lastIndexOrder;
    }
}
