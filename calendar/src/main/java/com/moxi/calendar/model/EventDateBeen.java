package com.moxi.calendar.model;

import java.util.Calendar;
import java.util.Date;

/**
 * 列表数据集合
 * Created by Administrator on 2016/9/8.
 */
public class EventDateBeen {
    /**
     * 事项数据
     */
    public EventBeen event;
    /**
     * 是否显示月份
     */
    public boolean isMonth;

    private Calendar calendar;
    /**
     * 周几时间
     */
    private String week;
    private String day;

    public EventDateBeen(EventBeen been, boolean isMonth) {
        this.event = been;
        this.isMonth = isMonth;
        Date date = new Date(Long.parseLong(been.saveTime));
        calendar = Calendar.getInstance();
        calendar.setTime(date);
    }

    public String getWeek() {
        if (week != null) return week;
        String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        mWay = "星期" + mWay;
        //当前时：HOUR_OF_DAY-24小时制；HOUR-12小时制
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //当前分
        int minute = calendar.get(Calendar.MINUTE);
        week = mWay + "\n" + hour + ":" + minute;
        return week;
    }

    public String getMonth() {
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    public String getDay() {
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }
    public String getTitle(){
        return event.name;
    }
    public String getRemark(){
        return event.remark;
    }

    @Override
    public String toString() {
        return "EventDateBeen{" +
                "event=" + event +
                ", isMonth=" + isMonth +
                ", calendar=" + calendar +
                ", week='" + week + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
