package com.moxi.calendar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xj on 2017/10/19.
 */

public class TimeStringUtils {
    /**
     * 一个月的开始和结束时间
     * @param year 年
     * @param month 月
     * @return 返回【0开始时间】，【1】结束时间
     */
    public static long[] getTimeLongs(int year, int month){
        long[] timeLong=new long[2];
        timeLong[0]=getTimeLong(year,month);
        if (month==12){
            year+=1;
            month=1;
        }else {
            month+=1;
        }
        timeLong[1]=getTimeLong(year,month);
//        for (int i = 0; i < timeLong.length; i++) {
//            Date date=new Date(timeLong[i]);
//            String strs="";
//            try {
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
//                strs=sdf.format(date);
//                APPLog.e("就是这么计算的=",strs);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return timeLong;
    }
    public static long getTimeLong(int year,int month){
        String time=year+"-"+month;
        long  lTime=System.currentTimeMillis();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM");
        Date dt2 = null;
        try {
            dt2 = sdf.parse(time);
            lTime = dt2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lTime;
    }
}
