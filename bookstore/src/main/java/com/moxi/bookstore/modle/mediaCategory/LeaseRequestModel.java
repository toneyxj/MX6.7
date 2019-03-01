package com.moxi.bookstore.modle.mediaCategory;

import java.io.Serializable;

/**
 * 请求借阅书籍
 * Created by xj on 2017/11/10.
 */

public class LeaseRequestModel implements Serializable{
    public final String action="mediaCategoryLeaf";
    /**
     * 选择分类code,默认DZS
     */
    private  String category="DZS";
    /**
     * 榜单类型
     */
    public String vipOnly="1";
    /**
     * 时间维度
     */
    public String dimension="hot_in_vip";
    public String startIndex="0";
    public String level="4";
    public int currentPage=0;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        currentPage=0;
    }

    public int getStart() {
        return currentPage*100;
    }

    public int getEnd() {
        return (currentPage+1)*100-1;
    }
}
