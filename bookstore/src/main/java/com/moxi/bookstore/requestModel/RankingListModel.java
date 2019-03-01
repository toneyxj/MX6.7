package com.moxi.bookstore.requestModel;

/**
 * Created by xj on 2017/10/16.
 */

public class RankingListModel {
    public final String action="mediaCategoryLeafNew";
    public final String category="DZS";
    /**
     * 榜单类型
     */
    public String rankType;
    /**
     * 时间维度
     */
    public String timeDimension="3";
    /**
     *付费类型，1付费，2免费
     */
    public String payType="1";


    public int currentPage=0;

    public void setTimeDimension(String timeDimension) {
        this.timeDimension = timeDimension;
        currentPage=0;
    }

    public void setPayType(String payType) {
        this.payType = payType;
        currentPage=0;
    }

    public int getStart() {
        return currentPage*100;
    }

    public int getEnd() {
        return (currentPage+1)*100-1;
    }
}
