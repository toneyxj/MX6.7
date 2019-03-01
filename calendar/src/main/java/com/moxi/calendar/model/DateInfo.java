package com.moxi.calendar.model;

/**
 * Created by Administrator on 2016/9/5.
 */
public class DateInfo {
    public int date;//当前日期
    public boolean isThisMonth;//是否是当月
    public boolean isWeekend;//是否是本周
    public boolean isHoliday;//是否是当天
    public String NongliDate; //农历日期

    @Override
    public String toString() {
        return "DateInfo{" +
                "date=" + date +
                ", isThisMonth=" + isThisMonth +
                ", isWeekend=" + isWeekend +
                ", isHoliday=" + isHoliday +
                ", NongliDate='" + NongliDate + '\'' +
                '}';
    }
}
