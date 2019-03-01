package com.moxi.haierc.castreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by King on 2017/7/27.
 */

public class ExternalStorageListener extends BroadcastReceiver {

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            //TODO:
            Log.e("ExternalStorage", Intent.ACTION_MEDIA_EJECT + "");
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            //TODO:
            Log.e("ExternalStorage", Intent.ACTION_MEDIA_MOUNTED + "");
            Intent intentex = new Intent("com.moxi.broadcast.external.action");
            localBroadcastManager.sendBroadcast(intentex);
        }
    }
}
