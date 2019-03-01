package com.moxi.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by Administrator on 2016/9/2.
 */
public class TimeBrodcast extends BroadcastReceiver {
    private static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;
    private static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_CHANGED;
    /**
     * 设置广播接收回调
     */
    private TimeOrDataChangeListener listener;

    public void setListener(TimeOrDataChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null == listener) return;
        //日期变化
        if (ACTION_DATE_CHANGED.equals(action)) {
            APPLog.e("日期TimeBrodcast");
            listener.dataChange();
        }
        //时间变化
        if (ACTION_TIME_CHANGED.equals(action)) {
            APPLog.e("时间TimeBrodcast");
            listener.timeChange();
        }
         if (action.equals(Intent.ACTION_TIME_TICK)){
            APPLog.e("TimeBrodcast时间改变");
        }
    }

    public interface TimeOrDataChangeListener {
        public void timeChange();

        public void dataChange();
    }
}
