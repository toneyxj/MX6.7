package com.moxi.bookstore.modle.mediaCategory;

/**
 * Created by xj on 2017/11/14.
 */

public class ChannelMonthlyStrategy {
    /**
     * 频道id
     */
    public long channelId;
    /**
     *包月类型id
     */
    public long id;
    /**
     *包月类型名称
     */
    public String name;
    /**
     *当前价格 
     */
    public int newPrice;
    /**
     *原价
     */
    public int originalPrice;
    /**
     *策略平台类型0-通过1-android,2-ios
     */
    public int platform;
    /**
     *策略时长 
     */
    public int strategyDays;
    /**
     *包月类型 
     */
    public int type;
}
