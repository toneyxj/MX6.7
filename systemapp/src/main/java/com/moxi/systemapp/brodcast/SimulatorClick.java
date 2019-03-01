package com.moxi.systemapp.brodcast;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by xj on 2018/7/31.
 */

public class SimulatorClick extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int action=intent.getIntExtra("type",3);
        APPLog.e("SimulatorClick-action",action);

        sendKeyEvent(action);
    }
    private  void sendKeyEvent(final int KeyCode) {
        new Thread() {     //不可在主线程中调用,所以这里使用线程发送
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}
