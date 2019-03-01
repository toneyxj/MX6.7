package com.moxi.calendar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间装换工具类
 * Created by Administrator on 2016/9/6.
 */
public class TimeChangeUtils {
    /**
     * 当前时间字符串
     */
    public static String currentTimeStr;
    /**
     * 获得装换后的时间
     * @param time 时间毫秒数
     * @return
     */
    public static String getTime(long time){
        Date date=new Date(time);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return fmt.format(date);
    }
    /**
     * 获得装换后的时间
     * @param time 时间毫秒数的字符串
     * @return
     */
    public static String getTime(String time){
        long t=Long.parseLong(time);
        return getTime(t);
    }

    /**
     * 获得时间装换成long
     * @param time 拼接后的时间字符串
     * @return
     */
    public static String getLongTime(String time) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = fmt.parse(time);
        return String.valueOf(date.getTime());
    }

    /**
     * 时间判断器
     * @param time
     */
    public static boolean judgeTime(String time){
        return getTime(time).equals(currentTimeStr);
    }

    /**
     * 判断提示是否已过期
     * @param time 时间时间
     * @return 如果已过期返回true
     */
    public static boolean judgeFormerly(String time){
        long times=Long.parseLong(time);
        return System.currentTimeMillis()>times;
    }
}
