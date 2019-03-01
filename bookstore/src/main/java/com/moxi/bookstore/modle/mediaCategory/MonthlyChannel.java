package com.moxi.bookstore.modle.mediaCategory;

import java.text.SimpleDateFormat;

/**
 * 个人租阅信息
 * Created by xj on 2017/11/14.
 */

public class MonthlyChannel {
    /**
     *频道ID
     */
    public long channelId;
    /**
     *频道创建时间
     */
    public long createDate;
    /**
     *频道描述
     */
    public String description;
    /**
     *是否购买包月权限 
     */
    public int hasBoughtMonthly;
    /**
     *图标
     */
    public String icon;
    /**
     *是否自动续订
     */
    public int isAutomaticallyRenew;
    /**
     *最后一次修改时间，用来分页
     */
    public long lastModifiedDate;
    /**
     *包月到期时间 
     */
    public long monthlyEndTime;
    /**
     *频道标题
     */
    public String title;
    /**
     * 上下架状态 
     */
    public int shelfStatus;

    /**
     * 是否是Vip
     * @return
     */
    public boolean isBoughtMonthly(){
        return hasBoughtMonthly>=1&&monthlyEndTime>=System.currentTimeMillis();
    }

    /**
     * 获得vip结束时间
     * @return
     */
    public String getBoughtMonthlyEndTime(){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateString = formatter.format(monthlyEndTime);
            return dateString;
//        return TimeUtils.getTimeShowString(monthlyEndTime,true);
    }
}
