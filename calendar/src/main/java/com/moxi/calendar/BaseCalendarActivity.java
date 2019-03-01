package com.moxi.calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.moxi.calendar.utils.TimeChangeUtils;
import com.moxi.calendar.utils.XJLunarCalendarUtils;
import com.moxi.calendar.utils.XJTimeUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;

import java.util.Calendar;

import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;

/**
 * Created by Administrator on 2016/9/2.
 */
public abstract class BaseCalendarActivity extends BaseActivity  {
    private static final String ACTION_TIME_TICK = Intent.ACTION_TIME_TICK;
    /**
     * 时间工具类
     */
    public XJTimeUtils xjTimeUtils;
    public XJLunarCalendarUtils xjLunar;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case ACTION_TIME_TICK://每秒时间监听
                    TimeChangeUtils.currentTimeStr=xjTimeUtils.currentTime(3);
                    APPLog.e("当前时间拼接串："+TimeChangeUtils.currentTimeStr);
                    //如果小时与分同时为0代表天发生了改变
//                    if ( xjTimeUtils.getMinute()==0&&xjTimeUtils.getHour()==0){
//                        dataChange();
//                    }else{
                        timeChange();
//                    }
                    break;
                case ACTION_TIMEZONE_CHANGED://时区改变监听
                    dataChange();
                    break;
                case ACTION_DATE_CHANGED://日期改变监听
//                    dataChange();
                    break;
                default:
                    break;
            }


        }

    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        /**
         * 注册广播监听，监听时间与日期的变化
         */
        IntentFilter intentFilter = new IntentFilter(ACTION_TIME_TICK);
        intentFilter.addAction(ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(ACTION_DATE_CHANGED);
        registerReceiver(receiver, intentFilter);

        xjTimeUtils=new XJTimeUtils();
        xjLunar=new XJLunarCalendarUtils(Calendar.getInstance());
    }

    public  abstract void   timeChange();
    public  abstract void   dataChange();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
