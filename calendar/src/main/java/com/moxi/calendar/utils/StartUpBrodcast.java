package com.moxi.calendar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moxi.calendar.LocationService;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class StartUpBrodcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationService.class));
    }
}
