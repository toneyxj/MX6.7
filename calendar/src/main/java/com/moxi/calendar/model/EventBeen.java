package com.moxi.calendar.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 事件数据类
 * Created by Administrator on 2016/9/6.
 */
public class EventBeen extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;
    /**
     * 保存事件日期
     */
    public String saveDate;
    /**
     * 保存事件具体提示时间
     */
    public String saveTime="";
    /**
     *事件名称
     */
    public String name;
    /**
     *事件是否提示，0否，1是,2已经响铃
     */
    public String whetherNotify="0";
    /**
     *设置的提示时间，0到时间提示，1提前5分钟，2提前15分钟，3提前30分钟，4提前一天
     */
    public String setNotify="0";
    /**
     * 提示时间毫秒数
     */
    public String notifyTime;
    /**
     *事件备注
     */
    public String remark;

    /**
     * 是否已经响铃
     */
    public boolean isDiabolo=false;
public EventBeen(){}
    public EventBeen(long id, String saveDate, String saveTime, String name, String whetherNotify, String setNotify, String notifyTime, String remark) {
        this.id = id;
        this.saveDate = saveDate;
        this.saveTime = saveTime;
        this.name = name;
        this.whetherNotify = whetherNotify;
        this.setNotify = setNotify;
        this.notifyTime = notifyTime;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "EventBeen{" +
                "id=" + id +
                ", saveDate='" + saveDate + '\'' +
                ", saveTime='" + saveTime + '\'' +
                ", name='" + name + '\'' +
                ", whetherNotify='" + whetherNotify + '\'' +
                ", setNotify='" + setNotify + '\'' +
                ", notifyTime='" + notifyTime + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
