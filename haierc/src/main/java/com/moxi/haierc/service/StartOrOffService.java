package com.moxi.haierc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.moxi.haierc.activity.OpenPasswordActivity;
import com.moxi.haierc.activity.TestActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.DevicePasswordCallBack;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.portal.CheckWifiLoginTask;
import com.mx.mxbase.utils.GetPasswordUtil;
import com.mx.mxbase.utils.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by xj on 2018/8/17.
 */

public class StartOrOffService extends Service {
    private static class MyHandler extends Handler {

        WeakReference<StartOrOffService> mReference = null;

        MyHandler(StartOrOffService activity) {
            this.mReference = new WeakReference<StartOrOffService>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            StartOrOffService outer = mReference.get();
            if (outer == null) {
                Log.e("outer is null");
                return;
            }

            outer.handleMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
        if (msg.what==11){//网络状态改变
            getWifiData();
        }

    }
    private MyHandler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        APPLog.e("StartOrOffService", "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        APPLog.e("StartOrOffService", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        APPLog.e("StartOrOffService", "onCreate：");
        startScreen(this);

        handler=new MyHandler(this);
        /**
         * 注册屏幕设备开屏/锁屏的状态监听
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(mScreenBroadcastReceiver, filter);

    }

    private BroadcastReceiver mScreenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            APPLog.e("网络连接状态", intent.getAction());
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                /**
                 * 屏幕亮
                 */
                APPLog.e("StartOrOffService", "屏幕亮");

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                /**
                 * 屏幕锁定
                 */
                APPLog.e("StartOrOffService", "屏幕锁定");
                //开启解锁界面
                startScreen(context);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                /**
                 * 屏幕解锁了且可以使用
                 */
                APPLog.e("StartOrOffService", "屏幕解锁了且可以使用");
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                //请求状态值改变
                handler.removeMessages(11);
                handler.sendEmptyMessageDelayed(11,1000);
            }
        }
    };
    private void getWifiData(){
        CheckWifiLoginTask.checkWifi(new CheckWifiLoginTask.ICheckWifiCallBack() {
            @Override
            public void portalNetWork(int type) {
                APPLog.e("portalNetWork",type);
                try {
                    switch (type) {
                        case 0://需要跳转
                            TestActivity.startWeb(StartOrOffService.this);
                            break;
                        case 1://请求成功,网络正常
//                            TestActivity.startWeb(StartOrOffService.this);
                            break;
                        case 2://请求失败
                            if (NetWorkUtil.isWifiConnected(StartOrOffService.this)) {
                                handler.sendEmptyMessageDelayed(11,60000);
                            }
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void startScreen(final Context context) {
        //判断是否开始屏幕锁
        new GetPasswordUtil(this, "lock_device_of_screen_open", new DevicePasswordCallBack() {
            @Override
            public void onSuccess(String password) {
                APPLog.e("GetPasswordUtil-data", password);
                try {
                    JSONObject object = new JSONObject(password);
                    String pas = object.getString("password");
                    int isOpen = object.getInt("isOpen");
                    if (isOpen == 1) {
                        Intent inLi = new Intent(context, OpenPasswordActivity.class);
                        inLi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        inLi.putExtra("password", pas);
                        context.startActivity(inLi);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFaile(String onFaile) {
                APPLog.e("GetPasswordUtil-onFaile", onFaile);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
}
