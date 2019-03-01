package com.moxi.calendar.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 时间工具类
 * Created by Administrator on 2016/9/2.
 */
public class XJTimeUtils {
public Calendar getCalendar(){
    Calendar c=Calendar.getInstance();
    return c;
}
    /**
     * 获得当前日期格式器
     *
     * @param type 获得的类型 1：2016年9月，2：今天 2016年9月2日，其他字符：2016-9-2 12:30
     * @return
     */
    public String currentTime(int type) {
        String spal = "";
        switch (type) {
            case 1:
                spal = "yyyy年MM月";
                break;
            case 2:
                spal = "今天\tyyyy年MM月dd";
                break;
            default:
                spal = "yyyy-MM-dd HH:mm";
                break;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat(spal);
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 获得年
     */
    public int getYear() {
        return getCalendar().get(Calendar.YEAR);
    }

    /**
     * 获得月
     * @return
     */
    public int getMonth() {
        return getCalendar().get(Calendar.MONTH)+1;
    }

    /**
     * 获得日
     * @return
     */
    public int getDay() {
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 获得分
     * @return
     */
    public int getMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }
    /**
     * 获得小时
     * @return
     */
    public int getHour() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得年月日主句级
     * @return 返回年 月 日
     */
    public int[] getAllDate(){
        int[] date=new int[3];
        date[0]=getYear();
        date[1]=getMonth();
        date[2]=getMonth();
        return date;
    }
}
