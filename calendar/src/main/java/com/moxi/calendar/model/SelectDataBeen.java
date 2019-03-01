package com.moxi.calendar.model;

/**
 * 选择日期数据
 * Created by 夏君 on 2017/4/27 0027.
 */

public class SelectDataBeen {
    /**
     * 时间索引
     */
    public int dataIndex;
    /**
     * 0上一年，1下一年，2年，3月
     */
    public int type;
    /**
     * 是否选中
     */
    public boolean Select=false;

    public SelectDataBeen(int dataIndex, int type, boolean select) {
        this.dataIndex = dataIndex;
        this.type = type;
        Select = select;
    }
}
